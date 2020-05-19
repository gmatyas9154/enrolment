package com.mgl.enrolment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "check_result")
public class CheckResult {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "valid_id_document")
    private Boolean validIdDocument;

    @Column(name = "credit_score")
    private Integer creditScore;

    @Column(name = "credit_risk")
    @Enumerated(EnumType.STRING)
    private CreditRisk creditRisk;

    @Column(name = "existing_client")
    private Boolean existingClient;


    public enum CreditRisk {
        NO_RISK,
        MEDIUM_RISK,
        HIGH_RISK
    }
}
