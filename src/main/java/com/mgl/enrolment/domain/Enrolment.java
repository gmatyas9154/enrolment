package com.mgl.enrolment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "enrolment")
@NoArgsConstructor
@AllArgsConstructor
public class Enrolment {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;

    @JoinColumn(name = "identity_document_id")
    @OneToOne(fetch = FetchType.EAGER)
    private IdentityDocument identityDocument;

    @JoinColumn(name = "check_result_id")
    @OneToOne(fetch = FetchType.EAGER)
    private CheckResult checkResult;

    @Column(name = "unsigned_pdf_url")
    private String unsignedPdfUrl;

    @JoinColumn(name = "document_id")
    @OneToOne(fetch = FetchType.EAGER)
    private Document document;

    public enum Status {
        INITIALIZED,
        VERIFIED,
        SIGNED
    }

}
