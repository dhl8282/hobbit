package com.example.hobbit.util;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable{
    private String source = "";
    private String id = "";
    private String lastname = "";
    private String firstname = "";
    private String username = "";
    private String email = "";
    private String gender = "";
    private String userId = "";
    private int totalLogin = 1;

    public User(String source, String id, String lastname, String firstname, String username) {
        super();
        this.source = source;
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.username = username;
        this.userId = source + id;
    }

    public int getTotalLogin() {
        return totalLogin;
    }

    public void setTotalLogin(int totalLogin) {
        this.totalLogin = totalLogin;
    }

    public String getUserId() {
        return userId;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
