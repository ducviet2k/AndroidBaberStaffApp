package com.example.androidbaberstaffapp.Model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

public class MyNotification {
    private String uid, title, conten;
    private boolean read;
    private Timestamp serverTimestamp;

    public MyNotification() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getConten() {
        return conten;
    }

    public void setConten(String conten) {
        this.conten = conten;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public Timestamp getServerTimestamp() {
        return serverTimestamp;
    }

    public void setServerTimestamp(Timestamp serverTimestamp) {
        this.serverTimestamp = serverTimestamp;
    }
}
