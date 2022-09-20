package com.example.vtafamily.Model;

public class Post {

    String user_id;
    String uname;
    String email;
    String udp;
    String pId;
    String pDescr;
    String pImage;
    String pTime;

    public Post() {
    }

    public Post(String user_id, String uname, String email, String udp, String pId, String pDescr, String pImage, String pTime) {
        this.user_id = user_id;
        this.uname = uname;
        this.email = email;
        this.udp = udp;
        this.pId = pId;
        this.pDescr = pDescr;
        this.pImage = pImage;
        this.pTime = pTime;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUdp() {
        return udp;
    }

    public void setUdp(String udp) {
        this.udp = udp;
    }

    public String getpId() {
        return pId;
    }

    public void setpId(String pId) {
        this.pId = pId;
    }

    public String getpDescr() {
        return pDescr;
    }

    public void setpDescr(String pDescr) {
        this.pDescr = pDescr;
    }

    public String getpImage() {
        return pImage;
    }

    public void setpImage(String pImage) {
        this.pImage = pImage;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }
}
