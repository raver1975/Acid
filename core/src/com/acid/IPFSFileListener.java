package com.klemstinegroup.sunshineblue.engine.util;

public interface IPFSFileListener {
    void downloaded(byte[] file);
    void downloadFailed(Throwable t);
}
