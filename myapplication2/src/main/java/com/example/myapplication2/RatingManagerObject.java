package com.example.myapplication2;

public class RatingManagerObject {
    String proposalId;
    String professionalUid;
    String customerUid;
    String professionalHasRatedCustomer;
    String customerHasRatedProfessional;


    public RatingManagerObject(String proposalId, String professionalUid, String customerUid, String professionalHasRatedCustomer, String customerHasRatedProfessional) {
        this.proposalId = proposalId;
        this.professionalUid = professionalUid;
        this.customerUid = customerUid;
        this.professionalHasRatedCustomer = professionalHasRatedCustomer;
        this.customerHasRatedProfessional = customerHasRatedProfessional;
    }

    public RatingManagerObject() {
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

    public String getProfessionalHasRatedCustomer() {
        return professionalHasRatedCustomer;
    }

    public void setProfessionalHasRatedCustomer(String professionalHasRatedCustomer) {
        this.professionalHasRatedCustomer = professionalHasRatedCustomer;
    }

    public String getCustomerHasRatedProfessional() {
        return customerHasRatedProfessional;
    }

    public void setCustomerHasRatedProfessional(String customerHasRatedProfessional) {
        this.customerHasRatedProfessional = customerHasRatedProfessional;
    }
}
