package com.antonitoari.rxlearning.models;

import java.io.Serializable;
import java.util.List;

import com.google.gson.Gson;

/**
 * Created by antonio tari
 */
public class AddressBookContact implements Serializable{
    private String id;
    private String name;
    private List<String> phoneList;
    private List<String> emailList;

    public AddressBookContact(final String id, final String name, final List<String> phoneList, final List<String> emailList) {
        this.id = id;
        this.name = name;
        this.phoneList = phoneList;
        this.emailList = emailList;
    }

    public List<String> getPhoneList() {
        return phoneList;
    }

    public List<String> getEmailList() {
        return emailList;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }
}