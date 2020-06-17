package com.saurabh.snapp.Receiver;

public class ReceiverObject {

    private String uid, email;
    private Boolean receive;

    public ReceiverObject(String uid, String email,Boolean receive)
    {
        this.uid=uid;
        this.email=email;
        this.receive = receive;
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getReceive() {
        return receive;
    }
    public void setReceive(Boolean receive) {
        this.receive = receive;
    }
}
