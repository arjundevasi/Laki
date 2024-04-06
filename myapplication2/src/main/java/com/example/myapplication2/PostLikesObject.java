package com.example.myapplication2;

public class PostLikesObject {
    String postId;
    String likedby;
    String timestamp;

    public PostLikesObject() {
    }

    public PostLikesObject(String postId, String likedby, String timestamp) {
        this.postId = postId;
        this.likedby = likedby;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getLikedby() {
        return likedby;
    }

    public void setLikedby(String likedby) {
        this.likedby = likedby;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
