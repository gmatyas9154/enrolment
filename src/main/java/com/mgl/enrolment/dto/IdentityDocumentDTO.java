package com.mgl.enrolment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.mgl.enrolment.domain.IdentityDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class IdentityDocumentDTO {

    private Long id;

    @NotNull(message = "Document Type is mandatory")
    private IdentityDocument.DocumentType documentType;

    @NotBlank(message = "Document id is mandatory")
    private String documentId;

    @NotBlank(message = "Last Name is mandatory")
    private String lastName;

    @NotBlank(message = "First Name is mandatory")
    private String firstName;

    private String cnp;

    private String sex;

    private String placeOfBirth;

    private String issuingAuthority;

    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate issuingDate;

    @NotNull(message = "Expiration Date is mandatory")
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate expirationDate;

    //### CI Specific Fields ##########

    private String address;

    private String parentFirstName;

    //### Passport Specific Fields ####

    private String countryCode;

    private String nationality;

    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate dateOfBirth;

    private String passportType;

    public enum DocumentType {
        CI,
        PASSPORT
    }
}
