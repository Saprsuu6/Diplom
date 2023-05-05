package com.example.instagram.DAOs;

import org.json.JSONException;
import org.json.JSONObject;

public class MainData {
    private String image;
    private String name;

    public void setImage(String image) {
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getImage() {
        return image;
    }

    public String getName() {
        return name;
    }

    public MainData(JSONObject object) throws JSONException {
        setImage(object.getString("download_url"));
        setName(object.getString("author"));
    }
}
