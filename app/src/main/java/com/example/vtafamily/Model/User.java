package com.example.vtafamily.Model;

public class User {

    private String id;
    private String username;
    private String mis;
    private String nic;
    private String imageURL;
    private String status;
    private String search;
    private String C_imageURL;

    public User(String id, String username, String mis, String nic, String imageURL,String C_imageURL,String status,String search) {
        this.id = id;
        this.username = username;
        this.mis = mis;
        this.nic = nic;
        this.imageURL = imageURL;
        this.C_imageURL = C_imageURL;
        this.status= status;
        this.search= search;
    }

    public User() {

    }

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

    public String getMis() {
        return mis;
    }

    public void setMis(String mis) {
        this.mis = mis;
    }

    public String getNic() {
        return nic;
    }

    public void setNic(String nic) {
        this.nic = nic;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public String getC_imageURL() {
        return C_imageURL;
    }

    public void setC_imageURL(String c_imageURL) {
        C_imageURL = c_imageURL;
    }
}
