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
import io.swagger.annotations.*;
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

@Api(value = "Enrolment Controller",
        tags = {"Enrolment Controller"})
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

    @ApiOperation(value = "Enroll a new client to the system",
            response = EnrolmentDTO.class,
            notes = "Creates a new enrolment, only identity document should be specified",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    @ApiResponses({
            @ApiResponse(code = 201, message = "Enrolment successfully created"),
            @ApiResponse(code = 400, message = "Submitted data failed validation"),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
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

    @ApiOperation(value = "Retrieve enrolments",
            response = EnrolmentDTO.class,
            notes = "Retrieves all the enrolments in the system",
            responseContainer = "List",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "Enrolments retrieved",
                    response = EnrolmentDTO.class,
                    responseContainer = "List"),
            @ApiResponse(code = 404, message = "No enrolments found"),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    @GetMapping
    public ResponseEntity<List<EnrolmentDTO>> fetchAllEnrolments() {
        List<Enrolment> enrolments = enrolmentService.fetchAll();
        List<EnrolmentDTO> enrolmentDTOS = enrolments.stream()
                .map(e -> modelMapper.map(e, EnrolmentDTO.class))
                .collect(Collectors.toList());
        return ResponseEntity.ok(enrolmentDTOS);
    }

    @ApiOperation(value = "Retrieve an enrolment",
            response = EnrolmentDTO.class,
            notes = "Retrieves a specific enrolment, identified by the path parameter",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200, message = "Enrolments retrieved", response = EnrolmentDTO.class),
            @ApiResponse(code = 404, message = "Enrolment with specified ID not found"),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    @GetMapping(path = "{enrolmentId}")
    public ResponseEntity<EnrolmentDTO> fetchEnrolment(@PathVariable(value = "enrolmentId") Long enrolmentId) {
        Optional<Enrolment> maybeEnrolment = enrolmentService.fetchById(enrolmentId);
        return maybeEnrolment
                .map(e -> ResponseEntity.ok(modelMapper.map(e, EnrolmentDTO.class)))
                .orElse(ResponseEntity.notFound().build());
    }

    @ApiOperation(value = "Initiate enrolment check",
            response = CheckResultDTO.class,
            notes = "Initiate the checking of the enrolment which verifies ID document validity, " +
                    "credit score, and whether or not the person enrolling is already a client",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "Enrolment check successfully completed",
                    response = CheckResultDTO.class),
            @ApiResponse(code = 404, message = "Enrolment with specified ID not found"),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    @PostMapping(path = "{enrolmentId}/check")
    public ResponseEntity<CheckResultDTO> clientCheck(@PathVariable("enrolmentId") Long enrolmentId) {
        CheckResult checkResult = clientCheckService.checkEnrolment(enrolmentId);
        return ResponseEntity.ok(modelMapper.map(checkResult, CheckResultDTO.class));
    }

    @ApiOperation(value = "Download Enrolment Result",
            notes = "Download either the enrolment document or the denial document, " +
                    "specifying the reason(s) for denial. In either case the document " +
                    "can be printed, should be signed and re-uploaded to the system.",
            response = byte[].class,
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "File is downloaded",
                    response = byte[].class),
            @ApiResponse(code = 404, message = "Enrolment with specified ID not found"),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    @GetMapping(path = "{enrolmentId}/file/unsigned")
    public ResponseEntity<Resource> downloadUnsigned(@PathVariable("enrolmentId") Long enrolmentId) {
        Resource pdfResource = pdfGeneratorService.generateUnsignedPDF(enrolmentId);
        String contentDisposition = String.format("attachment; filename=\"enrolment-%d-unsigned.pdf\"", enrolmentId);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(pdfResource);
    }

    @ApiOperation(value = "Upload Signed Document",
            notes = "Upload the signed enrolment or denial document",
            consumes = MediaType.APPLICATION_PDF_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE,
            response = DocumentDTO.class)
    @ApiResponses({
            @ApiResponse(code = 201,
                    message = "Document is uploaded",
                    response = DocumentDTO.class),
            @ApiResponse(code = 404, message = "Enrolment with specified ID not found"),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
    @PostMapping(path = "{enrolmentId}/document")
    public ResponseEntity<DocumentDTO> uploadDocument(@PathVariable("enrolmentId") Long enrolmentId,
                                                      @RequestParam("file") MultipartFile file) {
        Document document = documentService.uploadDocument(enrolmentId, file);
        return ResponseEntity.created(URI.create(document.getContentUrl()))
                .body(modelMapper.map(document, DocumentDTO.class));
    }

    @ApiOperation(value = "Download Signed Document",
            notes = "Download the signed copy of the enrolment or denial document",
            produces = MediaType.APPLICATION_PDF_VALUE)
    @ApiResponses({
            @ApiResponse(code = 200,
                    message = "File is downloaded"),
            @ApiResponse(code = 404, message = "Enrolment with specified ID not found"),
            @ApiResponse(code = 500, message = "Unexpected error")
    })
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
