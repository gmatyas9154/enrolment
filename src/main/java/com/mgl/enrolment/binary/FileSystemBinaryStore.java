package com.mgl.enrolment.binary;

import com.mgl.enrolment.faults.exceptions.EnrolmentException;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static com.mgl.enrolment.binary.BinaryStore.FILE_SYSTEM_STORE;

@Service(FILE_SYSTEM_STORE)
public class FileSystemBinaryStore implements BinaryStore {
    private static final String EXT = ".dat";

    @Value("${binary.file.system.folder}")
    private String storageFolder;

    @Override
    public String storeFileContent(byte[] data) {
        String externalId = UUID.randomUUID().toString();
        File actualFile = getFile(externalId);
        try {
            FileUtils.writeByteArrayToFile(actualFile, data);
        } catch (IOException e) {
            throw new EnrolmentException(e);
        }
        return externalId;
    }

    @Override
    public byte[] getFileContent(String externalId) {
        try {
            return FileUtils.readFileToByteArray(getFile(externalId));
        } catch (IOException e) {
            throw new EnrolmentException("Could not read file", e);
        }
    }

    @Override
    public void deleteFileContent(String externalId) {
        File actualFile = getFile(externalId);
        FileUtils.deleteQuietly(actualFile);
    }

    @Override
    public boolean exists(String externalId) {
        File actualFile = getFile(externalId);
        return actualFile.exists();
    }

    @Override
    public String getStoreType() {
        return FILE_SYSTEM_STORE;
    }

    private File getFile(String externalId) {
        File storageDir = new File(storageFolder);
        if (!storageDir.exists() && !storageDir.mkdir()) {
            throw new EnrolmentException("Cannot create storage directory");
        }
        String fileName = externalId + EXT;
        return new File (storageDir, fileName);
    }
}
