package com.guerra.russel.letmebee.Collection;

public class Users {

    private String id;
    private String email;
    private String firstname;
    private String lastname;
    private String address;
    private String phone;
    private int read;
    private int pending;
    private int approved;

    public Users() {}

    public Users(String id, String email, String firstname, String lastname, String address, String phone, int read, int pending, int approved) {
        this.id = id;
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.address = address;
        this.phone = phone;
        this.read = read;
        this.pending = pending;
        this.approved = approved;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public int getRead() {
        return read;
    }

    public int getPending() {
        return pending;
    }

    public int getApproved() {
        return approved;
    }
}
