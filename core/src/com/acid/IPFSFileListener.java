package com.acid;

public interface IPFSFileListener {
    void downloaded(byte[] file);
    void downloadFailed(Throwable t);
}
