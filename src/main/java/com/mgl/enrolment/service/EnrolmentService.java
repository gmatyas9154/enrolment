package com.mgl.enrolment.service;

import com.mgl.enrolment.dao.EnrolmentDao;
import com.mgl.enrolment.dao.IdentityDocumentDao;
import com.mgl.enrolment.domain.Enrolment;
import com.mgl.enrolment.domain.IdentityDocument;
import com.mgl.enrolment.faults.exceptions.DuplicateEnrolmentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

@Service
public class EnrolmentService {

    private final EnrolmentDao enrolmentDao;
    private final IdentityDocumentDao identityDocumentDao;
//    private final CheckResultDao checkResultDao;


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

        boolean existingEnrolment = enrolmentDao.exists(createIdDocumentFilter.apply(enrolment));
        if (existingEnrolment) {
            throw new DuplicateEnrolmentException("Duplicate identity document",
                    "identityDocument.documentType+identityDocument.documentId");
        }

        IdentityDocument dbIdentityDocument = identityDocumentDao.save(enrolment.getIdentityDocument());
        enrolment.setIdentityDocument(dbIdentityDocument);
        enrolment.setStatus(Enrolment.Status.INITIALIZED);
        return enrolmentDao.saveAndFlush(enrolment);
    }

    private static final Function<Enrolment, Example<Enrolment>> createIdDocumentFilter = e -> Example.of(Enrolment.builder()
            .identityDocument(IdentityDocument.builder()
                    .documentType(e.getIdentityDocument().getDocumentType())
                    .documentId(e.getIdentityDocument().getDocumentId())
                    .build())
            .build());

}
