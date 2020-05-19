package com.mgl.enrolment.dao;

import com.mgl.enrolment.domain.IdentityDocument;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IdentityDocumentDao extends JpaRepository<IdentityDocument, Long> {
}
