package com.mgl.enrolment.utils;

import com.mgl.enrolment.dto.EnrolmentDTO;
import com.mgl.enrolment.dto.IdentityDocumentDTO;
import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDate;

public class TestUtils {

    public static EnrolmentDTO buildEnrolmentValid() {
        LocalDate notExpired = LocalDate.now().plusYears(1);
        IdentityDocumentDTO idDto = IdentityDocumentDTO.builder()
                .documentType(IdentityDocumentDTO.DocumentType.CI)
                .documentId("CJ-111001")
                .expirationDate(notExpired)
                .firstName(RandomStringUtils.randomAlphabetic(10))
                .lastName(RandomStringUtils.randomAlphabetic(10))
                .build();
        return EnrolmentDTO.builder()
                .identityDocument(idDto)
                .build();
    }

    /**
     * The mock services work as follows:
     *  - credit score : (Last 3 digits of doc id) % 200
     *  - existing : (doc id is even)  = true
     *
     * @return enrolmentDto
     */
    public static EnrolmentDTO allChecksInvalid() {
        LocalDate expired = LocalDate.now().minusYears(1);
        IdentityDocumentDTO idDto = IdentityDocumentDTO.builder()
                .documentType(IdentityDocumentDTO.DocumentType.CI)
                .documentId("CJ-111198")
                .expirationDate(expired)
                .firstName(RandomStringUtils.randomAlphabetic(10))
                .lastName(RandomStringUtils.randomAlphabetic(10))
                .build();
        return EnrolmentDTO.builder()
                .identityDocument(idDto)
                .build();
    }
}
