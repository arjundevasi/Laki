package com.example.myapplication2;

public class PostObject {
    String uid;
    String timestamp;
    String postId;
    String text;
    String image1path;
    String image2path;
    String image3path;

    public PostObject() {
    }

    public PostObject(String uid, String timestamp, String postId, String text, String image1path, String image2path, String image3path) {
        this.uid = uid;
        this.timestamp = timestamp;
        this.postId = postId;
        this.text = text;
        this.image1path = image1path;
        this.image2path = image2path;
        this.image3path = image3path;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage1path() {
        return image1path;
    }

    public void setImage1path(String image1path) {
        this.image1path = image1path;
    }

    public String getImage2path() {
        return image2path;
    }

    public void setImage2path(String image2path) {
        this.image2path = image2path;
    }

    public String getImage3path() {
        return image3path;
    }

    public void setImage3path(String image3path) {
        this.image3path = image3path;
    }
}
