package com.example.myapplication2;

public class PostTrendObject {
    String postId;
    long likes;
    long comments;
    long shares;
    long views;

    public PostTrendObject() {
    }

    public PostTrendObject(String postId, long likes, long comments, long shares, long views) {
        this.postId = postId;
        this.likes = likes;
        this.comments = comments;
        this.shares = shares;
        this.views = views;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public long getLikes() {
        return likes;
    }

    public void setLikes(long likes) {
        this.likes = likes;
    }

    public long getComments() {
        return comments;
    }

    public void setComments(long comments) {
        this.comments = comments;
    }

    public long getShares() {
        return shares;
    }

    public void setShares(long shares) {
        this.shares = shares;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }
}
