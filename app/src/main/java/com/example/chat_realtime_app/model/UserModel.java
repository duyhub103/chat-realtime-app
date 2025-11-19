package com.example.chat_realtime_app.model;

import com.google.firebase.Timestamp;

import java.util.List;

public class UserModel {
    private String userId;
    private String username;
    private String phone;
    private String email;
    private String avatarUrl;
    private String fcmToken;
    private Timestamp createdTimestamp;
    private List<String> searchKeywords;
    private String gender;
    private String birthdate;

    public String getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(String birthday) {
        this.birthdate = birthday;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public UserModel() {
    }

    //Constructor login bằng PHONE
    public UserModel(String phone, String username, Timestamp createdTimestamp, String userId) {
        this.phone = phone;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    //Constructor login bằng EMAIL
    public UserModel(String email, String username, Timestamp createdTimestamp, String userId, boolean isEmailLogin) {
        this.email = email;
        this.username = username;
        this.createdTimestamp = createdTimestamp;
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }

    public Timestamp getCreatedTimestamp() {
        return createdTimestamp;
    }

    public void setCreatedTimestamp(Timestamp createdTimestamp) {
        this.createdTimestamp = createdTimestamp;
    }

    public List<String> getSearchKeywords() {
        return searchKeywords;
    }

    public void setSearchKeywords(List<String> searchKeywords) {
        this.searchKeywords = searchKeywords;
    }
}
