package com.example.myapplication2;

public class NotificationsObject {
    String senderUid;
    String receiverUid;
    String proposalId;
    String title;
    String timestamp;

    String isSeen;

    String notificationId;

    String counterProposalId;

    String pendingProposalId;

    String ongoingProposalId;

    String completedProposalId;

    String type;

    String postId;



    public NotificationsObject() {
    }

    public NotificationsObject(String senderUid, String receiverUid, String proposalId, String title, String timestamp,
                               String isSeen, String notificationId, String counterProposalId, String pendingProposalId,
                               String ongoingProposalId, String completedProposalId,
                               String type, String postId) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.proposalId = proposalId;
        this.title = title;
        this.timestamp = timestamp;
        this.isSeen = isSeen;
        this.notificationId = notificationId;
        this.counterProposalId = counterProposalId;
        this.pendingProposalId = pendingProposalId;
        this.ongoingProposalId = ongoingProposalId;
        this.completedProposalId = completedProposalId;
        this.type = type;
        this.postId = postId;
    }

    public String getSenderUid() {
        return senderUid;
    }

    public void setSenderUid(String senderUid) {
        this.senderUid = senderUid;
    }

    public String getReceiverUid() {
        return receiverUid;
    }

    public void setReceiverUid(String receiverUid) {
        this.receiverUid = receiverUid;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }


    public String getCounterProposalId() {
        return counterProposalId;
    }

    public void setCounterProposalId(String counterProposalId) {
        this.counterProposalId = counterProposalId;
    }

    public String getPendingProposalId() {
        return pendingProposalId;
    }

    public void setPendingProposalId(String pendingProposalId) {
        this.pendingProposalId = pendingProposalId;
    }

    public String getOngoingProposalId() {
        return ongoingProposalId;
    }

    public void setOngoingProposalId(String ongoingProposalId) {
        this.ongoingProposalId = ongoingProposalId;
    }

    public String getCompletedProposalId() {
        return completedProposalId;
    }

    public void setCompletedProposalId(String completedProposalId) {
        this.completedProposalId = completedProposalId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
