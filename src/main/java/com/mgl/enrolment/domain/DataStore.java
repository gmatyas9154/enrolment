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
@Table(name = "data_storage")
public class DataStore {

    @Id
    private String id;

    @Lob
    @Column(name = "data", columnDefinition = "BLOB")
    private byte[] data;

}
