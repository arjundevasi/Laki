package com.example.myapplication2;

public class RegisterUser {
    private String fullName;
    private String phoneNumber;
    private String username;
    private String location;

    private String profileUrl;

    public RegisterUser() {
    }

    public RegisterUser(String fullName, String phoneNumber, String username, String location, String profileUrl) {
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.location = location;
        this.profileUrl = profileUrl;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }


    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
