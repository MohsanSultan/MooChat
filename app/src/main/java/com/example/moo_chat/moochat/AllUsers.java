package com.example.moo_chat.moochat;

public class AllUsers {

    public String id;
    public String name;
    public String status;
    public String image;
    public String thumb_img;

    public AllUsers(String thumb_img) {
        this.thumb_img = thumb_img;
    }

    public String getThumb_img() {
        return thumb_img;
    }

    public void setThumb_img(String thumb_img) {
        this.thumb_img = thumb_img;
    }

    public AllUsers(){

    }

    public AllUsers(String id, String name, String status, String image){
        this.id = id;
        this.name = name;
        this.status = status;
        this.image = image;
    }

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
