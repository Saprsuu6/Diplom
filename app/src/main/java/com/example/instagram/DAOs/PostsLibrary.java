package com.example.instagram.DAOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostsLibrary {
    private final ArrayList<Post> dataArrayList = new ArrayList<>();

    public ArrayList<Post> getDataArrayList() {
        return dataArrayList;
    }

    public void setDataArrayList(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            dataArrayList.add(new Post(object));
        }
    }
}
