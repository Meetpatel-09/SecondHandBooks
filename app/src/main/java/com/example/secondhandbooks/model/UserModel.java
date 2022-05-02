package com.example.secondhandbooks.model;

public class UserModel {
    private String name;
    private String email;
    private String id;
    private String imageUrl;
    private String phone;

    public UserModel() {
    }

    public UserModel(String name, String email, String id, String imageUrl, String phone) {
        this.name = name;
        this.email = email;
        this.id = id;
        this.imageUrl = imageUrl;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
