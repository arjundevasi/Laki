package com.example.myapplication2;

public class ChatIndexObject {
    private String conversationId;
    private String user1;
    private String user2;

    public ChatIndexObject(){}

    public ChatIndexObject(String conversationId, String user1, String user2) {
        this.conversationId = conversationId;
        this.user1 = user1;
        this.user2 = user2;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getUser1() {
        return user1;
    }

    public void setUser1(String user1) {
        this.user1 = user1;
    }

    public String getUser2() {
        return user2;
    }

    public void setUser2(String user2) {
        this.user2 = user2;
    }

}
