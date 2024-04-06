package com.example.myapplication2;

public class RatingAndReviewObject {
    int rating;
    String review;
    String proposalId;
    String professionalUid;
    String customerUid;

    String ratedByUid;

    String ratedByName;

    public RatingAndReviewObject() {
    }

    public RatingAndReviewObject(int rating, String review, String proposalId, String professionalUid, String customerUid, String ratedByUid,String ratedByName) {
        this.rating = rating;
        this.review = review;
        this.proposalId = proposalId;
        this.professionalUid = professionalUid;
        this.customerUid = customerUid;
        this.ratedByUid = ratedByUid;
        this.ratedByName = ratedByName;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
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

    public String getRatedByUid() {
        return ratedByUid;
    }

    public void setRatedByUid(String ratedByUid) {
        this.ratedByUid = ratedByUid;
    }

    public String getRatedByName() {
        return ratedByName;
    }

    public void setRatedByName(String ratedByName) {
        this.ratedByName = ratedByName;
    }
}
