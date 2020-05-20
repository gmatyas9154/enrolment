package com.mgl.enrolment.binary;

import com.mgl.enrolment.dao.DataStoreDao;
import com.mgl.enrolment.domain.DataStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.mgl.enrolment.binary.BinaryStore.DB_STORE;

@Service(DB_STORE)
public class DatabaseBinaryStore implements BinaryStore {

    private final DataStoreDao dataStoreDao;

    @Autowired
    public DatabaseBinaryStore(DataStoreDao dataStoreDao) {
        this.dataStoreDao = dataStoreDao;
    }

    @Override
    public String storeFileContent(byte[] data) {
        String id = UUID.randomUUID().toString();
        DataStore ds = DataStore.builder()
                .id(id)
                .data(data)
                .build();
        dataStoreDao.save(ds);
        return id;
    }

    @Override
    public byte[] getFileContent(String externalId) {
        return dataStoreDao.getOne(externalId).getData();
    }

    @Override
    public void deleteFileContent(String externalId) {
        dataStoreDao.deleteById(externalId);
    }

    @Override
    public boolean exists(String externalId) {
        return dataStoreDao.existsById(externalId);
    }

    @Override
    public String getStoreType() {
        return DB_STORE;
    }
}
