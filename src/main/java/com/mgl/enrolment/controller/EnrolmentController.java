package com.mgl.enrolment.controller;

import com.mgl.enrolment.domain.CheckResult;
import com.mgl.enrolment.domain.Document;
import com.mgl.enrolment.domain.Enrolment;
import com.mgl.enrolment.dto.CheckResultDTO;
import com.mgl.enrolment.dto.DocumentDTO;
import com.mgl.enrolment.dto.EnrolmentDTO;
import com.mgl.enrolment.service.ClientCheckService;
import com.mgl.enrolment.service.DocumentService;
import com.mgl.enrolment.service.EnrolmentService;
import com.mgl.enrolment.service.PDFGeneratorService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.activation.MimetypesFileTypeMap;
import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrolment")
@Validated
public class EnrolmentController {

    private final EnrolmentService enrolmentService;
    private final ClientCheckService clientCheckService;
    private final ModelMapper modelMapper;
    private final PDFGeneratorService pdfGeneratorService;
    private final DocumentService documentService;

    private final MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();

    @Autowired
    public EnrolmentController(EnrolmentService enrolmentService, ClientCheckService clientCheckService, ModelMapper modelMapper, PDFGeneratorService pdfGeneratorService, DocumentService documentService) {
        this.enrolmentService = enrolmentService;
        this.clientCheckService = clientCheckService;
        this.modelMapper = modelMapper;
        this.pdfGeneratorService = pdfGeneratorService;
        this.documentService = documentService;
    }

    @PostMapping
    public ResponseEntity<EnrolmentDTO> enrollClient(@RequestBody @Valid EnrolmentDTO enrolmentDTO) {
        // we should not have document or check result when creating
        enrolmentDTO.setCheckResult(null);
        enrolmentDTO.setDocument(null);
        Enrolment enrolmentToAdd = modelMapper.map(enrolmentDTO, Enrolment.class);

        Enrolment newEnrolment = enrolmentService.enrollClient(enrolmentToAdd);

        return ResponseEntity.created(URI.create("/api/enrolment/" + newEnrolment.getId()))
                .body(modelMapper.map(newEnrolment, EnrolmentDTO.class));
    }

    @GetMapping
    public ResponseEntity<List<EnrolmentDTO>> fetchAllEnrolments() {
        List<Enrolment> enrolments = enrolmentService.fetchAll();
        List<EnrolmentDTO> enrolmentDTOS = enrolments.stream()
                .map(e -> modelMapper.map(e, EnrolmentDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(enrolmentDTOS);
    }

    @GetMapping(path = "{enrolmentId}")
    public ResponseEntity<EnrolmentDTO> fetchEnrolment(@PathVariable("enrolmentId") Long enrolmentId) {
        Optional<Enrolment> maybeEnrolment = enrolmentService.fetchById(enrolmentId);
        return maybeEnrolment
                .map(e -> ResponseEntity.ok(modelMapper.map(e, EnrolmentDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(path = "{enrolmentId}/check")
    public ResponseEntity<CheckResultDTO> clientCheck(@PathVariable("enrolmentId") Long enrolmentId) {
        CheckResult checkResult = clientCheckService.checkEnrolment(enrolmentId);
        return ResponseEntity.ok(modelMapper.map(checkResult, CheckResultDTO.class));
    }


    @GetMapping(path = "{enrolmentId}/file/unsigned")
    public ResponseEntity<Resource> downloadUnsigned(@PathVariable("enrolmentId") Long enrolmentId) {
        Resource pdfResource = pdfGeneratorService.generateUnsignedPDF(enrolmentId);
        String contentDisposition = String.format("attachment; filename=\"enrolment-%d-unsigned.pdf\"", enrolmentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(pdfResource);
    }

    @PostMapping(path = "{enrolmentId}/document")
    public ResponseEntity<DocumentDTO> uploadDocument(@PathVariable("enrolmentId") Long enrolmentId,
                                                   @RequestParam("file") MultipartFile file) {
        Document document = documentService.uploadDocument(enrolmentId, file);
        return ResponseEntity.ok(modelMapper.map(document, DocumentDTO.class));
    }

    @GetMapping(path = "{enrolmentId}/document")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("enrolmentId") Long enrolmentId) {
        Document document = documentService.getDocumentFileContent(enrolmentId);
        Resource resource = new ByteArrayResource(document.getContent());
        String fileName = document.getFileName();
        String contentDisposition = String.format("attachment; filename=\"%s\"", fileName);
        MediaType mediaType = guessMediaType(fileName);
        return ResponseEntity.ok()
                .contentType(mediaType)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    private MediaType guessMediaType(String fileName) {
        try {
            String contentType = fileTypeMap.getContentType(fileName);
            return MediaType.valueOf(contentType);
        } catch (Exception e) {
            //default to octet stream if cannot guess
            return MediaType.APPLICATION_OCTET_STREAM;
        }
    }

}
