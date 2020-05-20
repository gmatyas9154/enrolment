package com.mgl.enrolment.service;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.itextpdf.text.DocumentException;
import com.mgl.enrolment.dao.EnrolmentDao;
import com.mgl.enrolment.domain.CheckResult;
import com.mgl.enrolment.domain.Enrolment;
import com.mgl.enrolment.domain.IdentityDocument;
import com.mgl.enrolment.faults.exceptions.EnrolmentException;
import com.mgl.enrolment.faults.exceptions.IncorrectStateException;
import com.mgl.enrolment.faults.exceptions.NotFoundException;
import lombok.Builder;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.w3c.tidy.Tidy;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class PDFGeneratorService {

    private static final String APPROVED_TEMPLATE = "templates/pdf/accepted/pdf.html";
    private static final String REJECTED_TEMPLATE = "templates/pdf/rejected/pdf.html";
    private static final String DATE_FORMAT = "dd.MM.yyyy";

    private final EnrolmentDao enrolmentDao;
    private final MustacheFactory mustacheFactory = new DefaultMustacheFactory();



    @Autowired
    public PDFGeneratorService(EnrolmentDao enrolmentDao) {
        this.enrolmentDao = enrolmentDao;
    }

    public Resource generateUnsignedPDF(Long enrolmentId) {
        Optional<Enrolment> maybeEnrolment = enrolmentDao.findById(enrolmentId);
        if (maybeEnrolment.isEmpty()) {
            throw new NotFoundException("Specified enrolment not found");
        }
        Enrolment dbEnrolment = maybeEnrolment.get();
        if (dbEnrolment.getStatus() == Enrolment.Status.INITIALIZED) {
            throw new IncorrectStateException("Enrolment needs to be checked before downloading file for signing");
        }

        IdentityDocument identityDocument = dbEnrolment.getIdentityDocument();

        CheckResult checkResult = dbEnrolment.getCheckResult();
        boolean isRejected = checkResult.getExistingClient()
                || !checkResult.getValidIdDocument()
                || checkResult.getCreditScore() > 99;

        String template = isRejected ? REJECTED_TEMPLATE : APPROVED_TEMPLATE;

        PdfData data = PdfData.builder()
                .name(identityDocument.getFirstName() + " " + identityDocument.getLastName())
                .documentType(identityDocument.getDocumentType().getDisplayName())
                .documentId(identityDocument.getDocumentId())
                .documentExpired(!checkResult.getValidIdDocument())
                .expirationDate(identityDocument.getExpirationDate().format(DateTimeFormatter.ofPattern(DATE_FORMAT)))
                .badCredit(CheckResult.CreditRisk.HIGH_RISK.equals(checkResult.getCreditRisk()))
                .creditScore(checkResult.getCreditScore())
                .isDuplicate(checkResult.getExistingClient())
                .build();



        String xHtml;
        try (StringWriter writer = new StringWriter()) {
            Mustache mustache = mustacheFactory.compile(template);
            mustache.execute(writer, data).flush();
            String html = writer.toString();
            xHtml = convertToXhtml(html);
        } catch (IOException e) {
            throw new EnrolmentException(e);
        }

        ITextRenderer renderer = new ITextRenderer();
        renderer.setDocumentFromString(xHtml);
        renderer.layout();

        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            renderer.createPDF(outputStream);
            byte[] pdfContent = outputStream.toByteArray();
            return new InputStreamResource(new ByteArrayInputStream(pdfContent));
        } catch (DocumentException | IOException e) {
            throw new EnrolmentException(e);
        }
    }


    private String convertToXhtml(String html) {
        Tidy tidy = new Tidy();
        tidy.setInputEncoding(StandardCharsets.UTF_8.displayName());
        tidy.setOutputEncoding(StandardCharsets.UTF_8.displayName());
        tidy.setXHTML(true);
        tidy.setShowWarnings(false);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(html.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        tidy.parseDOM(inputStream, outputStream);
        return outputStream.toString(StandardCharsets.UTF_8);
    }

    @Getter
    @Builder
    public static class PdfData {
        private final String name;
        private final String documentType;
        private final String documentId;

        private final Boolean documentExpired;
        private final String expirationDate;

        private final Boolean badCredit;
        private final Integer creditScore;

        private final Boolean isDuplicate;
    }
}
