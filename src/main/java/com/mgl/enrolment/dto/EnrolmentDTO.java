package com.mgl.enrolment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("Object representing the enrolment")
public class EnrolmentDTO {
    @ApiModelProperty(value = "Unique Identifier", notes = "Not accepted on POST")
    private Long id;
    @ApiModelProperty(value = "Status of enrolment", notes = "Not accepted on POST")
    private StatusDTO status;

    @NotNull
    @Valid
    @ApiModelProperty(required = true, value = "Identity document", name = "Identity document")
    private IdentityDocumentDTO identityDocument;

    @ApiModelProperty(value = "Identity Check Result", notes = "Not accepted on POST")
    private CheckResultDTO checkResult;

    @ApiModelProperty(value = "Url of unsigned PDF", notes = "Not accepted on POST")
    private String unsignedPdfUrl;

    @ApiModelProperty(value = "Object representing the signed document", notes = "Not accepted on POST")
    private DocumentDTO document;

    public enum StatusDTO {
        INITIALIZED,
        VERIFIED,
        SIGNED
    }
}
