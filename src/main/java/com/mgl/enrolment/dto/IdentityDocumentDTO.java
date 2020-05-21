package com.mgl.enrolment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel(description = "Object describing the identity document", value = "IdentityDocument")
public class IdentityDocumentDTO {

    @ApiModelProperty
    private Long id;

    @ApiModelProperty(required = true,
            notes = "Type of ID document, like passport, or CI",
            allowableValues = "CI,PASSPORT")
    @NotNull(message = "Document Type is mandatory")
    private DocumentType documentType;

    @ApiModelProperty(required = true,
            notes = "Unique identifier of the document, like passport number, or CI serial number")
    @NotBlank(message = "Document id is mandatory")
    private String documentId;

    @ApiModelProperty(required = true)
    @NotBlank(message = "Last Name is mandatory")
    private String lastName;

    @ApiModelProperty(required = true)
    @NotBlank(message = "First Name is mandatory")
    private String firstName;

    @ApiModelProperty
    private String cnp;

    @ApiModelProperty
    private String sex;

    @ApiModelProperty
    private String placeOfBirth;

    @ApiModelProperty
    private String issuingAuthority;

    @JsonFormat(pattern="dd.MM.yyyy")
    @ApiModelProperty(dataType = "Date", example = "26.09.1985")
    private LocalDate issuingDate;

    @NotNull(message = "Expiration Date is mandatory")
    @JsonFormat(pattern="dd.MM.yyyy")
    @ApiModelProperty(required = true, dataType = "Date", example = "26.09.1985")
    private LocalDate expirationDate;

    //### CI Specific Fields ##########
    @ApiModelProperty
    private String address;

    @ApiModelProperty
    private String parentFirstName;

    //### Passport Specific Fields ####

    @ApiModelProperty
    private String countryCode;

    @ApiModelProperty
    private String nationality;

    @ApiModelProperty(dataType = "Date", example = "26.09.1985")
    @JsonFormat(pattern="dd.MM.yyyy")
    private LocalDate dateOfBirth;

    @ApiModelProperty
    private String passportType;

    public enum DocumentType {
        CI,
        PASSPORT
    }
}
