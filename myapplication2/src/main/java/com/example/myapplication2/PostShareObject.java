package com.example.myapplication2;

public class PostShareObject {
    String postId;
    String sharedBy;
    String timestamp;

    public PostShareObject() {
    }

    public PostShareObject(String postId, String sharedBy, String timestamp) {
        this.postId = postId;
        this.sharedBy = sharedBy;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(String sharedBy) {
        this.sharedBy = sharedBy;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
