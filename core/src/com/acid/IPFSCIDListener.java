package com.klemstinegroup.sunshineblue.engine.util;

public interface IPFSCIDListener {
    void cid(String cid);
    void uploadFailed(Throwable t);
}
