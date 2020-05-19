package com.mgl.enrolment.dao;

import com.mgl.enrolment.domain.Enrolment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EnrolmentDao extends JpaRepository<Enrolment, Long> {
}
