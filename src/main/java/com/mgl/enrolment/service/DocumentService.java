package com.mgl.enrolment.service;

import com.mgl.enrolment.binary.BinaryStore;
import com.mgl.enrolment.dao.DataStoreDao;
import com.mgl.enrolment.dao.DocumentDao;
import com.mgl.enrolment.dao.EnrolmentDao;
import com.mgl.enrolment.domain.Document;
import com.mgl.enrolment.domain.Enrolment;
import com.mgl.enrolment.faults.exceptions.EnrolmentException;
import com.mgl.enrolment.faults.exceptions.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Service
public class DocumentService {

    private final BinaryStore defaultStore;
    private final DocumentDao documentDao;
    private final EnrolmentDao enrolmentDao;
    private final ApplicationContext context;

    @Autowired
    public DocumentService(BinaryStore defaultStore, DataStoreDao dataStoreDao, DocumentDao documentDao, EnrolmentDao enrolmentDao, ApplicationContext context) {
        this.defaultStore = defaultStore;
        this.documentDao = documentDao;
        this.enrolmentDao = enrolmentDao;
        this.context = context;
    }

    public Document uploadDocument(Long enrolmentId, MultipartFile multipartFile) {
        Optional<Enrolment> maybeEnrolment = enrolmentDao.findById(enrolmentId);
        if (maybeEnrolment.isEmpty()) {
            throw new NotFoundException("Count not find enrolment to upload to");
        }

        // check if we already have a document
        Enrolment dbEnrolment = maybeEnrolment.get();
        if (dbEnrolment.getDocument() != null) {
            Document oldDocument = dbEnrolment.getDocument();
            dbEnrolment.setDocument(null);
            deleteDocument(oldDocument);
        }

        byte[] content;
        String fileName = multipartFile.getOriginalFilename();
        try {
            content = multipartFile.getBytes();
        } catch (IOException e) {
            throw new EnrolmentException("Unable to read uploaded file", e);
        }

        String externalId = defaultStore.storeFileContent(content);

        String documentUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("api/enrolment/")
                .path(enrolmentId.toString())
                .path("/document")
                .toUriString();

        Document newDocument = Document.builder()
                .externalId(externalId)
                .fileName(fileName)
                .storeType(defaultStore.getStoreType())
                .contentUrl(documentUri)
                .build();

        newDocument = documentDao.save(newDocument);

        dbEnrolment.setDocument(newDocument);
        enrolmentDao.saveAndFlush(dbEnrolment);
        return newDocument;
    }

    public Document getDocumentFileContent(Long enrolmentId) {
        Optional<Enrolment> maybeEnrolment = enrolmentDao.findById(enrolmentId);
        if (maybeEnrolment.isEmpty()) {
            throw new NotFoundException("Count not find enrolment to upload to");
        }
        Enrolment dbEnrolment = maybeEnrolment.get();
        Document document = dbEnrolment.getDocument();
        if (document == null) {
            throw new NotFoundException("No uploaded document for enrolment with id: " + enrolmentId);
        }

        String storeType = document.getStoreType();
        BinaryStore binaryStore = context.getBean(storeType, BinaryStore.class);
        byte[] fileContent = binaryStore.getFileContent(document.getExternalId());
        document.setContent(fileContent);
        return document;
    }

    private void deleteDocument(Document oldDocument) {
        String storeType = oldDocument.getStoreType();
        BinaryStore binaryStore = context.getBean(storeType, BinaryStore.class);
        binaryStore.deleteFileContent(oldDocument.getExternalId());
        documentDao.delete(oldDocument);
    }
}
