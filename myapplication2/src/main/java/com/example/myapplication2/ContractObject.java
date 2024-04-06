package com.example.myapplication2;

public class ContractObject {
    String proposalId;
    String professionalUid;
    String customerUid;

    public ContractObject(String proposalId, String professionalUid, String customerUid) {
        this.proposalId = proposalId;
        this.professionalUid = professionalUid;
        this.customerUid = customerUid;
    }

    public ContractObject() {
    }

    public String getProposalId() {
        return proposalId;
    }

    public void setProposalId(String proposalId) {
        this.proposalId = proposalId;
    }

    public String getProfessionalUid() {
        return professionalUid;
    }

    public void setProfessionalUid(String professionalUid) {
        this.professionalUid = professionalUid;
    }

    public String getCustomerUid() {
        return customerUid;
    }

    public void setCustomerUid(String customerUid) {
        this.customerUid = customerUid;
    }
}
