package com.example.androidbaberstaffapp.Model;

import com.example.androidbaberstaffapp.Common.Common;

public class MyToken {
     private  String token,user;
     private Common.TOKEN_TYPE tokentype;

    public MyToken() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Common.TOKEN_TYPE getTokentype() {
        return tokentype;
    }

    public void setTokentype(Common.TOKEN_TYPE tokentype) {
        this.tokentype = tokentype;
    }
}
