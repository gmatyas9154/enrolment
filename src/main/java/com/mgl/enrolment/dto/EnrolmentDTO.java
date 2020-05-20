package com.mgl.enrolment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnrolmentDTO {
    private Long id;
    private StatusDTO status;

    @NotNull
    @Valid
    private IdentityDocumentDTO identityDocument;

    private CheckResultDTO checkResult;

    private String unsignedPdfUrl;

    private DocumentDTO document;

    public enum StatusDTO {
        INITIALIZED,
        VERIFIED,
        SIGNED
    }
}
