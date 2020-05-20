package com.mgl.enrolment.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Builder
@Entity
@Table(name = "enrolment_doc")
@NoArgsConstructor
@AllArgsConstructor
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "store_type")
    private String storeType;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "content_url")
    private String contentUrl;

    private transient byte[] content;
}
