package com.example.moo_chat.moochat;

public class FriendRequests {

public String request_type;


    public FriendRequests(){

    }

    public FriendRequests(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }
}
