package com.mgl.enrolment.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel("Object that represents an uploaded document")
public class DocumentDTO {

    @ApiModelProperty
    private Long id;
    @ApiModelProperty
    private String externalId;
    @ApiModelProperty
    private String storeType;
    @ApiModelProperty(value = "Download url", notes = "URL that can be used to download this file")
    private String contentUrl;

    @JsonIgnore
    @ApiModelProperty(hidden = true)
    private transient byte[] content;

}
