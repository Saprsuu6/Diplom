package com.example.instagram.DAOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PostsLibrary {
    private final ArrayList<Post> posts = new ArrayList<>();

    public ArrayList<Post> getDataArrayList() {
        return posts;
    }

    public void setDataArrayList(JSONArray array) throws JSONException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            posts.add(new Post(object));
        }
    }
}
