package com.example.instagram.DAOs;

public class PhoneBookContact {
    public String getName() {
        return name;
    }

    public String[] getPhoneNumbers() {
        return phoneNumbers;
    }

    private final String name;

    private final String[] phoneNumbers;

    public PhoneBookContact(String name, String[] phoneNumbers) {
        this.name = name;
        this.phoneNumbers = phoneNumbers;
    }

}
