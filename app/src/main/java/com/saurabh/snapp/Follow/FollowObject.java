package com.saurabh.snapp.Follow;

public class FollowObject {

    private String uid, email;

    public FollowObject(String uid, String email)
    {
        this.uid=uid;
        this.email=email;
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
}
