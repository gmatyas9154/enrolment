package com.mgl.enrolment.dao;

import com.mgl.enrolment.domain.Document;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentDao extends JpaRepository<Document, Long> {
}
