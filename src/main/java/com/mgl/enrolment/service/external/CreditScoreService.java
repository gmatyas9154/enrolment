package com.mgl.enrolment.service.external;

import com.mgl.enrolment.domain.IdentityDocument;
import com.mgl.enrolment.dto.external.CreditScoreDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CreditScoreService {

    private RestTemplate restTemplate;

    @Value("${credit.score.service.host}")
    private String creditScoreServiceHost;

    @Autowired
    public CreditScoreService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public CreditScoreDTO checkScore(IdentityDocument identityDocument) {
        String documentType = identityDocument.getDocumentType().name().toLowerCase();
        String docId = identityDocument.getDocumentId();
        String scoreUrl = String.format( "%s/%s/%s",
                creditScoreServiceHost,
                documentType,
                docId);
        return restTemplate.getForObject(scoreUrl, CreditScoreDTO.class);
    }
}
