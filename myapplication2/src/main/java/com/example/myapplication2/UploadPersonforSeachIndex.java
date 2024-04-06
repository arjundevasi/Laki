package com.example.myapplication2;

public class UploadPersonforSeachIndex {
    private String fullname;
    private String username;
    private String category;

    private String profileUrl;

    private long totalContracts;

    private double averageRating;

    private long totalRatingCount;

    private String uid;

    private String location;

    private String year;

    private String services;

    private String fcmToken;


    public UploadPersonforSeachIndex() {
    }


    public UploadPersonforSeachIndex(String fullname, String username, String category, String profileUrl, long totalContracts, double averageRating, long totalRatingCount, String uid, String location, String year, String services, String fcmToken) {
        this.fullname = fullname;
        this.username = username;
        this.category = category;
        this.profileUrl = profileUrl;
        this.totalContracts = totalContracts;
        this.averageRating = averageRating;
        this.totalRatingCount = totalRatingCount;
        this.uid = uid;
        this.location = location;
        this.year = year;
        this.services = services;
        this.fcmToken = fcmToken;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getFullname() {
        return fullname;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCategory() {
        return category;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public long getTotalContracts() {
        return totalContracts;
    }

    public void setTotalContracts(long totalContracts) {
        this.totalContracts = totalContracts;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public long getTotalRatingCount() {
        return totalRatingCount;
    }

    public void setTotalRatingCount(long totalRatingCount) {
        this.totalRatingCount = totalRatingCount;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getServices() {
        return services;
    }

    public void setServices(String services) {
        this.services = services;
    }

    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
}
