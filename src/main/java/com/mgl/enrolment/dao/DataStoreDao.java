package com.mgl.enrolment.dao;

import com.mgl.enrolment.domain.DataStore;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DataStoreDao extends JpaRepository<DataStore, String> {
}
