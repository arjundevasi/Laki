package com.example.myapplication2;

public class AllMessageListObject {

    String fullName;
    String profileUrl;
    String oppositeUid;
    String conversationId;
    String lastMessage;
    String timestamp;

    Boolean isSeen;

    public AllMessageListObject() {}

    public AllMessageListObject(String fullName, String profileUrl, String oppositeUid, String conversationId, String lastMessage, String timestamp,Boolean isSeen) {
        this.fullName = fullName;
        this.profileUrl = profileUrl;
        this.oppositeUid = oppositeUid;
        this.conversationId = conversationId;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getOppositeUid() {
        return oppositeUid;
    }

    public void setOppositeUid(String oppositeUid) {
        this.oppositeUid = oppositeUid;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public Boolean getSeenBoolean() {
        return isSeen;
    }

    public void setSeenBoolean(Boolean seen) {
        isSeen = seen;
    }
}
