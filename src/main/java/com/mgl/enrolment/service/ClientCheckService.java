package com.mgl.enrolment.service;

import com.mgl.enrolment.dao.CheckResultDao;
import com.mgl.enrolment.dao.EnrolmentDao;
import com.mgl.enrolment.domain.CheckResult;
import com.mgl.enrolment.domain.Enrolment;
import com.mgl.enrolment.dto.external.CreditScoreDTO;
import com.mgl.enrolment.errors.EnrolmentException;
import com.mgl.enrolment.service.external.CreditScoreService;
import com.mgl.enrolment.service.external.ExistenceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

@Service
public class ClientCheckService {

    private static final ZoneId ROMANIA_ZONE_ID = ZoneId.of("Europe/Bucharest");

    private final EnrolmentDao enrolmentDao;
    private final CheckResultDao checkResultDao;
    private final CreditScoreService creditScoreService;
    private final ExistenceService existenceService;

    @Autowired
    public ClientCheckService(EnrolmentDao enrolmentDao, CheckResultDao checkResultDao, CreditScoreService creditScoreService, ExistenceService existenceService) {
        this.enrolmentDao = enrolmentDao;
        this.checkResultDao = checkResultDao;
        this.creditScoreService = creditScoreService;
        this.existenceService = existenceService;
    }

    public CheckResult checkEnrolment(Long enrolmentId) {
        Optional<Enrolment> maybeEnrolment = enrolmentDao.findById(enrolmentId);

        if (maybeEnrolment.isEmpty()) {
            throw new EnrolmentException("Cannot run check operation: Enrolment not found!");
        }

        Enrolment enrolment = maybeEnrolment.get();
        CheckResult oldCheckResult = enrolment.getCheckResult();

        CheckResult checkResult = new CheckResult();

        //run verifications
        verifyIdentityDocument(enrolment, checkResult);
        verifyCreditScore(enrolment, checkResult);
        verifyDuplicate(enrolment, checkResult);


        // save new checkResult
        CheckResult dbCheckResult = checkResultDao.save(checkResult);
        enrolment.setCheckResult(dbCheckResult);

        enrolment.setStatus(Enrolment.Status.VERIFIED);
        enrolmentDao.save(enrolment);
        if (oldCheckResult != null) {
            checkResultDao.delete(oldCheckResult);
        }

        checkResultDao.flush();
        return dbCheckResult;
    }

    private void verifyDuplicate(Enrolment enrolment, CheckResult checkResult) {
        Boolean exists = existenceService.exists(enrolment.getIdentityDocument());
        checkResult.setExistingClient(exists);
    }

    private void verifyCreditScore(Enrolment enrolment, CheckResult checkResult) {
        CreditScoreDTO creditScoreDTO = creditScoreService.checkScore(enrolment.getIdentityDocument());
        Integer score = creditScoreDTO.getScore();
        CheckResult.CreditRisk risk = CheckResult.CreditRisk.HIGH_RISK;
        if (score <= 20) {
            risk = CheckResult.CreditRisk.NO_RISK;
        } else if (score <= 99) {
            risk = CheckResult.CreditRisk.MEDIUM_RISK;
        }
        checkResult.setCreditScore(score);
        checkResult.setCreditRisk(risk);
    }

    private void verifyIdentityDocument(Enrolment enrolment, CheckResult checkResult) {
        LocalDate expirationDate = enrolment.getIdentityDocument().getExpirationDate();

        LocalDate today = LocalDate.now(ROMANIA_ZONE_ID);

        checkResult.setValidIdDocument(expirationDate.isAfter(today));
    }
}
