package com.android.ran.network;

import com.android.ran.model.User;

import java.io.Serializable;

public class SignInResponse implements Serializable {

    private String token;
    private String expires;
    private User user;

    public String token() {
        return token;
    }

    public String expires() {
        return expires;
    }

    public User user() {
        return user;
    }
}