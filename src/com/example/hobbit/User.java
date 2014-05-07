package com.example.hobbit;

import java.io.Serializable;

@SuppressWarnings("serial")
public class User implements Serializable{
    private String source, id, lastname, firstname, username, email, gender, userId;

    public User(String source, String id, String lastname, String firstname, String username) {
        super();
        this.source = source;
        this.id = id;
        this.lastname = lastname;
        this.firstname = firstname;
        this.username = username;
        this.userId = source + id;
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
