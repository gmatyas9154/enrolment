package com.mgl.enrolment.domain;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "identity_document")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IdentityDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "document_type")
    @Enumerated(EnumType.STRING)
    private DocumentType documentType;

    @Column(name = "document_id")
    private String documentId;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "cnp")
    private String cnp;

    @Column(name = "sex")
    private String sex;

    @Column(name = "place_of_birth")
    private String placeOfBirth;

    @Column(name = "issuing_authority")
    private String issuingAuthority;

    @Column(name = "issuing_date")
    private LocalDate issuingDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    //### CI Specific Fields ##########

    @Column(name = "address")
    private String address;

    @Column(name = "parent_first_name")
    private String parentFirstName;

    //### Passport Specific Fields ####

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "nationality")
    private String nationality;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "passport_type")
    private String passportType;

    public enum DocumentType {
        CI ("Ci"),
        PASSPORT ("Passport");

        String displayName;

        DocumentType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}
