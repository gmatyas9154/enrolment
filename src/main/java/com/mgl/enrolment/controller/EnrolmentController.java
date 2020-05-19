package com.mgl.enrolment.controller;

import com.mgl.enrolment.dto.EnrolmentDTO;
import com.mgl.enrolment.domain.Enrolment;
import com.mgl.enrolment.service.EnrolmentService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/enrolment")
@Validated
public class EnrolmentController {

    private EnrolmentService enrolmentService;
    private ModelMapper modelMapper;

    @Autowired
    public EnrolmentController(EnrolmentService enrolmentService, ModelMapper modelMapper) {
        this.enrolmentService = enrolmentService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    public ResponseEntity<EnrolmentDTO> enrollClient(@RequestBody @Valid EnrolmentDTO enrolmentDTO) {
        Enrolment enrolmentToAdd = modelMapper.map(enrolmentDTO, Enrolment.class);
        Enrolment newEnrolment = enrolmentService.enrollClient(enrolmentToAdd);
        EnrolmentDTO newEnrolmentDto = modelMapper.map(newEnrolment, EnrolmentDTO.class);
        return ResponseEntity.created(URI.create("/api/enrolment/" + newEnrolment.getId()))
                .body(newEnrolmentDto);
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


}
