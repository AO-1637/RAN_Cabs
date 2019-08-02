package com.android.ran.model;

import com.android.ran.network.SignInResponse;

import java.io.Serializable;

public class User implements Serializable {

    private static User instance = null;
    private String _id;
    private String username;
    private String email;
    private String contact;
    private String alternateContact;
    private Cab[] cabsBooked;

    private Boolean res;
    private String response;
    private SignInResponse token;

    private User() {
    }

    public User(String username, String email, String contact, String alternateContact) {
        this.username = username;
        this.email = email;
        this.contact = contact;
        this.alternateContact = alternateContact;
    }

    public static User getInstance() {
        if (instance == null) {
            instance = new User();
        }
        return instance;
    }

    public String get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAlternateContact() {
        return alternateContact;
    }

    public void setAlternateContact(String alternateContact) {
        this.alternateContact = alternateContact;
    }

    public Cab[] getCabsBooked() {
        return cabsBooked;
    }

    public void setCabsBooked(Cab[] cabsBooked) {
        this.cabsBooked = cabsBooked;
    }

    public Boolean res() {
        return res;
    }

    public String response() {
        return response;
    }

    public SignInResponse token() {
        return token;
    }
}