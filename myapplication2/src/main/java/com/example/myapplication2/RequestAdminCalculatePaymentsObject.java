package com.example.myapplication2;

public class RequestAdminCalculatePaymentsObject {
    String proposalId;
    String professionalId;

    long timestamp;

    public RequestAdminCalculatePaymentsObject() {}

    public RequestAdminCalculatePaymentsObject(String proposalId, String professionalId, long timestamp) {
        this.proposalId = proposalId;
        this.professionalId = professionalId;
        this.timestamp = timestamp;
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getProfessionalId() {
        return professionalId;
    }

    public void setProfessionalId(String professionalId) {
        this.professionalId = professionalId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
