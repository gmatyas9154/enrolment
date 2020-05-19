package com.mgl.enrolment.service;

import com.mgl.enrolment.dao.EnrolmentDao;
import com.mgl.enrolment.dao.IdentityDocumentDao;
import com.mgl.enrolment.domain.Enrolment;
import com.mgl.enrolment.domain.IdentityDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EnrolmentService {

    private final EnrolmentDao enrolmentDao;
    private final IdentityDocumentDao identityDocumentDao;

    @Autowired
    public EnrolmentService(EnrolmentDao enrolmentDao, IdentityDocumentDao identityDocumentDao) {
        this.enrolmentDao = enrolmentDao;
        this.identityDocumentDao = identityDocumentDao;
    }

    public Optional<Enrolment> fetchById(Long enrolmentId) {
        return enrolmentDao.findById(enrolmentId);
    }

    public List<Enrolment> fetchAll() {
        return enrolmentDao.findAll();
    }

    public Enrolment enrollClient(Enrolment enrolment) {
        IdentityDocument dbIdentityDocument = identityDocumentDao.save(enrolment.getIdentityDocument());
        enrolment.setIdentityDocument(dbIdentityDocument);
        return enrolmentDao.saveAndFlush(enrolment);
    }




}
