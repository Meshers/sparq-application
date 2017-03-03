package com.sparq.application.model;

import java.io.Serializable;
import java.sql.Blob;

/**
 * Created by sarahcs on 2/13/2017.
 */

public class UserItem implements Serializable {

    private int userId;
    /**
     * 1: Teacher
     * 2: Student
     */
    private int userType;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private String emailId;
    private String description;
    private Blob userImage;

    public UserItem() {
    }

    public UserItem(int userId, int userType, String description, String emailId, String lastName, String password, String username, String firstName, Blob userImage) {
        this.userId = userId;
        this.userType = userType;
        this.description = description;
        this.emailId = emailId;
        this.lastName = lastName;
        this.password = password;
        this.username = username;
        this.firstName = firstName;
        this.userImage = userImage;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getUserImage() {
        return userImage;
    }

    public void setUserImage(Blob userImage) {
        this.userImage = userImage;
    }


}
