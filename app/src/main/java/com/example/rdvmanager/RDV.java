package com.example.rdvmanager;

import android.net.Uri;

import androidx.annotation.NonNull;

import java.util.Date;

public class RDV {
    private int id;
    private String title;
    private String date;
    private String time;
    private String contact;
    private String address;
    private String phoneNumber;
    private boolean isDone;


    public RDV() {
        this.title = null;
        this.date = null;
        this.time = null;
        this.contact = null;
        this.address = null;
        this.phoneNumber = null;
        this.isDone = false;
    }
    public RDV(String title, String date, String time, String contact, String address, String phoneNumber, boolean isDone) {
        this.title = title;
        this.date = date;
        this.time = time;
        this.contact = contact;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isDone = isDone;
    }

    public RDV(int id, String title, String date, String time, String contact, String address, String phoneNumber, boolean isDone) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.time = time;
        this.contact = contact;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.isDone = isDone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() { return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone(boolean done) {
        isDone = done;
    }

    public String toString() {
        return "Title: " + getTitle() + "\n" +
                "Date: " + getDate() + "\n" +
                "Time: " + getTime() + "\n" +
                "Contact: " + getContact() + "\n" +
                "Address: " + getAddress() + "\n" +
                "Phone: " + getPhoneNumber();
    }

    public Uri getAddressUri() {
        String uri = "geo:0,0?q=" + getAddress();
        return Uri.parse(uri);
    }
}
