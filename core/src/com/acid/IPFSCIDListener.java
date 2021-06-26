package com.acid;

public interface IPFSCIDListener {
    void cid(String cid);
    void uploadFailed(Throwable t);
}
