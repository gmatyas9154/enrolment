package com.mgl.enrolment.binary;

public interface BinaryStore {

    String DB_STORE = "db-store";
    String FILE_SYSTEM_STORE = "file-system-store";

    String storeFileContent(byte[] data);
    byte[] getFileContent(String externalId);
    void deleteFileContent(String externalId);
    boolean exists(String externalId);
    String getStoreType();
}
