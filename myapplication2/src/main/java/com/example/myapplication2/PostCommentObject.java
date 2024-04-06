package com.example.myapplication2;

public class PostCommentObject {
    String postId;
    String commentBy;
    String comment;
    String timestamp;

    public PostCommentObject() {
    }

    public PostCommentObject(String postId, String commentBy, String comment, String timestamp) {
        this.postId = postId;
        this.commentBy = commentBy;
        this.comment = comment;
        this.timestamp = timestamp;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getCommentBy() {
        return commentBy;
    }

    public void setCommentBy(String commentBy) {
        this.commentBy = commentBy;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
