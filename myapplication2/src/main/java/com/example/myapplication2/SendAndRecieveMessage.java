package com.example.myapplication2;

import java.util.HashMap;
import java.util.Map;

public class SendAndRecieveMessage {

    private String messageText;
    private String timestamp;
    private String senderId;

    private String receiverId;

    private String seenTimestamp;
    private Boolean seenBoolean;

    private String isImage;

    private String imageUrl;

    private String messageId;


    public SendAndRecieveMessage() {
    }

    public SendAndRecieveMessage(String messageText, String timestamp, String senderId, String recieverId, String seenTimestamp, Boolean seenBoolean,String isImage,String imageUrl,String messageId) {
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.senderId = senderId;
        this.receiverId = recieverId;
        this.seenTimestamp = seenTimestamp;
        this.seenBoolean = seenBoolean;
        this.isImage = isImage;
        this.imageUrl = imageUrl;
        this.messageId = messageId;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSeenTimestamp(String seenTimestamp) {
        this.seenTimestamp = seenTimestamp;
    }

    public String getSeenTimestamp() {
        return seenTimestamp;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setSeenBoolean(Boolean seenBoolean) {
        this.seenBoolean = seenBoolean;
    }

    public Boolean getSeenBoolean() {
        return seenBoolean;
    }

    public String getIsImage() {
        return isImage;
    }

    public void setIsImage(String isImage) {
        this.isImage = isImage;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }
}
