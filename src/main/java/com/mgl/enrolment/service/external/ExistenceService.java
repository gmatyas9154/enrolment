package com.mgl.enrolment.service.external;

import com.mgl.enrolment.domain.IdentityDocument;
import com.mgl.enrolment.dto.external.ExistDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
public class ExistenceService {

    private RestTemplate restTemplate;

    @Value("${exist.service.host}")
    private String creditScoreServiceHost;

    @Autowired
    public ExistenceService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Boolean exists(IdentityDocument identityDocument) {
        String documentType = identityDocument.getDocumentType().name().toLowerCase();
        String docId = identityDocument.getDocumentId();
        String existServiceUrl = String.format( "%s/%s/%s",
                creditScoreServiceHost,
                documentType,
                docId);
        ExistDTO existDTO = restTemplate.getForObject(existServiceUrl, ExistDTO.class);
        return Optional.ofNullable(existDTO).map(ExistDTO::getExist).orElse(Boolean.FALSE);
    }
}
