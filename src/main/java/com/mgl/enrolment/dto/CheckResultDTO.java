package com.mgl.enrolment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
public class CheckResultDTO {

    private Long id;

    private Boolean validIdDocument;

    private Integer creditScore;

    private CreditRisk creditRisk;

    private Boolean existingClient;

    public enum CreditRisk {
        NO_RISK,
        MEDIUM_RISK,
        HIGH_RISK
    }
}
