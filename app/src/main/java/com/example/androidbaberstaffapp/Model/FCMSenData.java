package com.example.androidbaberstaffapp.Model;

import java.util.Map;

public class FCMSenData {
    private String to;
    private Map<String,String> data;

    public FCMSenData() {
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Map<String, String> getData() {
        return data;
    }

    public void setData(Map<String, String> data) {
        this.data = data;
    }
}
