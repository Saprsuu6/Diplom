package com.example.instagram.DAOs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class CommentsLibrary {
    private final List<Comment> comments = new ArrayList<>();

    public List<Comment> getCommentList() {
        return comments;
    }

    public void setDataArrayList(JSONArray array) throws JSONException, ParseException {
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            comments.add(new Comment(object));
        }
    }
}
