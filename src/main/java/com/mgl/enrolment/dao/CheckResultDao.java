package com.mgl.enrolment.dao;

import com.mgl.enrolment.domain.CheckResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CheckResultDao extends JpaRepository<CheckResult, Long> {
}
