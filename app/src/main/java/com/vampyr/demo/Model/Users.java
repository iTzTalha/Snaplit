package com.vampyr.demo.Model;

public class Users {

    private String id;
    private String username;
    private String fullname;
    private String imageurl;
    private String bio;


    public Users(String id, String username, String fullname, String imageurl, String brio) {
        this.id = id;
        this.username = username;
        this.fullname = fullname;
        this.imageurl = imageurl;
        this.bio = bio;
    }

    public Users() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getBrio() {
        return bio;
    }

    public void setBrio(String brio) {
        this.bio = brio;
    }
}
