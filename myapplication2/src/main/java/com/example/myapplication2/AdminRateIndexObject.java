package com.example.myapplication2;

public class AdminRateIndexObject {
    String userId;
    String proposalId;
    String uploadedBy;
    String hasCalculated;

    public AdminRateIndexObject(){}

    public AdminRateIndexObject(String userId, String proposalId, String uploadedBy, String hasCalculated){
        this.userId = userId;
        this.proposalId = proposalId;
        this.uploadedBy = uploadedBy;
        this.hasCalculated = hasCalculated;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public String getHasCalculated() {
        return hasCalculated;
    }

    public void setHasCalculated(String hasCalculated) {
        this.hasCalculated = hasCalculated;
    }
}
