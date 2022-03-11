package com.example.cosc2657_a2_s3811248_nguyentranphu;

public class User {
    private String name;
    private String address;
    private String email;
    private boolean isNewUser;

    public User(String name, String email, String address) {
        this.name = name;
        this.email = email;
        this.address = address;
        this.isNewUser = true;

    }

    public boolean getIsNewUser() {
        return isNewUser;
    }

    public void setIsNewUser(boolean newUser) {
        this.isNewUser = newUser;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
