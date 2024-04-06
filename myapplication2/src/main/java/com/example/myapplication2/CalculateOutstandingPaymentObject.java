package com.example.myapplication2;

public class CalculateOutstandingPaymentObject {
    String totalOutstanding;
    String totalEarned;
    long timestamp;
    public CalculateOutstandingPaymentObject() {}

    public CalculateOutstandingPaymentObject(String totalOutstanding, String totalEarned, long timestamp) {
        this.totalOutstanding = totalOutstanding;
        this.totalEarned = totalEarned;
        this.timestamp = timestamp;
    }

    public String getTotalOutstanding() {
        return totalOutstanding;
    }

    public void setTotalOutstanding(String totalOutstanding) {
        this.totalOutstanding = totalOutstanding;
    }

    public String getTotalEarned() {
        return totalEarned;
    }

    public void setTotalEarned(String totalEarned) {
        this.totalEarned = totalEarned;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
