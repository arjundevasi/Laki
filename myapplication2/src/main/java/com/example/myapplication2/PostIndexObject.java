package com.example.myapplication2;

public class PostIndexObject {
    String postId;
    String uid;
    String timestamp;

    public PostIndexObject() {
    }

    public PostIndexObject(String postId, String uid, String timestamp) {
        this.postId = postId;
        this.uid = uid;
        this.timestamp = timestamp;

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
}
