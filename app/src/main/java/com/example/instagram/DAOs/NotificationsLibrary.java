package com.example.instagram.DAOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class NotificationsLibrary {
    private final List<Notification> notifications = new ArrayList<>();

    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setDataArrayList(JSONArray array) throws JSONException, ParseException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            notifications.add(new Notification(object));
        }
    }
}
