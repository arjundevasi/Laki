package com.example.myapplication2;

public class PendingProposalObject {
    String senderUid;
    String receiverUid;
    String proposalId;
    String title;
    String details;
    String budget;

    String timestamp;

    String hasAccepted;

    String counterProposalId;

    String acceptedBy;

    String professionalId;

    String customerId;

    String startDate;

    public PendingProposalObject() {
    }

    public PendingProposalObject(String senderUid, String receiverUid, String proposalId, String title, String details, String budget, String timestamp, String hasAccepted, String counterProposalId, String acceptedBy, String professionalId, String customerId,String startDate) {
        this.senderUid = senderUid;
        this.receiverUid = receiverUid;
        this.proposalId = proposalId;
        this.title = title;
        this.details = details;
        this.budget = budget;
        this.timestamp = timestamp;
        this.hasAccepted = hasAccepted;
        this.counterProposalId = counterProposalId;
        this.acceptedBy = acceptedBy;
        this.professionalId = professionalId;
        this.customerId = customerId;
        this.startDate = startDate;
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

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHasAccepted() {
        return hasAccepted;
    }

    public void setHasAccepted(String hasAccepted) {
        this.hasAccepted = hasAccepted;
    }

    public String getCounterProposalId() {
        return counterProposalId;
    }

    public void setCounterProposalId(String counterProposalId) {
        this.counterProposalId = counterProposalId;
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }
}
