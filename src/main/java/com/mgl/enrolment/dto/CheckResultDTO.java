package com.mgl.enrolment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties(ignoreUnknown = true)
@ApiModel(description = "Object describing the result of the enrolment check", value = "CheckResult")
public class CheckResultDTO {

    @ApiModelProperty
    private Long id;

    @ApiModelProperty
    private Boolean validIdDocument;

    @ApiModelProperty
    private Integer creditScore;

    @ApiModelProperty
    private CreditRisk creditRisk;

    @ApiModelProperty
    private Boolean existingClient;

    public enum CreditRisk {
        NO_RISK,
        MEDIUM_RISK,
        HIGH_RISK
    }
}
