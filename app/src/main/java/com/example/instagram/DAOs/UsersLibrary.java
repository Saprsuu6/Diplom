package com.example.instagram.DAOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class UsersLibrary {
    private final ArrayList<User> users = new ArrayList<>();

    public ArrayList<User> getDataArrayList() {
        return users;
    }

    public void setDataArrayList(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            users.add(new User(object));
        }
    }
}
