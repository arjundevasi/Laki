package com.example.myapplication2;

public class AcceptProposalObject {
    String senderUid;
    String receiverUid;
    String proposalId;
    String title;
    String details;
    String budget;

    String ongoingTimestamp;

    String hasAccepted;


    String acceptedBy;

    String professionalId;

    String customerId;

    String professionalMarkedAsCompleted;

    String professionalMarkedCompleteTimestamp;

    String startDate;

    public AcceptProposalObject(String senderUid, String receiverUid, String proposalId, String title, String details, String budget, String ongoingTimestamp, String hasAccepted, String acceptedBy, String professionalId, String customerId, String professionalMarkedAsCompleted, String professionalMarkedCompleteTimestamp,String startDate) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.proposalId = proposalId;
        this.title = title;
        this.details = details;
        this.budget = budget;
        this.ongoingTimestamp = ongoingTimestamp;
        this.hasAccepted = hasAccepted;
        this.acceptedBy = acceptedBy;
        this.professionalId = professionalId;
        this.customerId = customerId;
        this.professionalMarkedAsCompleted = professionalMarkedAsCompleted;
        this.professionalMarkedCompleteTimestamp = professionalMarkedCompleteTimestamp;
        this.startDate = startDate;
    }

    public AcceptProposalObject() {
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getBudget() {
        return budget;
    }

    public void setBudget(String budget) {
        this.budget = budget;
    }

    public String getOngoingTimestamp() {
        return ongoingTimestamp;
    }

    public void setOngoingTimestamp(String ongoingTimestamp) {
        this.ongoingTimestamp = ongoingTimestamp;
    }

    public String getHasAccepted() {
        return hasAccepted;
    }

    public void setHasAccepted(String hasAccepted) {
        this.hasAccepted = hasAccepted;
    }


    public String getAcceptedBy() {
        return acceptedBy;
    }

    public void setAcceptedBy(String acceptedBy) {
        this.acceptedBy = acceptedBy;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getProfessionalMarkedAsCompleted() {
        return professionalMarkedAsCompleted;
    }

    public void setProfessionalMarkedAsCompleted(String professionalMarkedAsCompleted) {
        this.professionalMarkedAsCompleted = professionalMarkedAsCompleted;
    }

    public String getProfessionalMarkedCompleteTimestamp() {
        return professionalMarkedCompleteTimestamp;
    }

    public void setProfessionalMarkedCompleteTimestamp(String professionalMarkedCompleteTimestamp) {
        this.professionalMarkedCompleteTimestamp = professionalMarkedCompleteTimestamp;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}