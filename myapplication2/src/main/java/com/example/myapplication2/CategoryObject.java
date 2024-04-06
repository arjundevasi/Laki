package com.example.myapplication2;

public class CategoryObject {
    private String category;
    private String url;

    public CategoryObject() {
    }

    public CategoryObject(String category, String url) {
        this.category = category;
        this.url = url;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
