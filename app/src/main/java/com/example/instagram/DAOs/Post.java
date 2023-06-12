package com.example.instagram.DAOs;

import androidx.annotation.Nullable;

import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Post {
    private Date dateOfAdd;
    private String author;
    private String description;
    private int likes;
    @Nullable
    private String resourceImg;
    @Nullable
    private String resourceVideo;
    @Nullable
    private String metadata;
    @Nullable
    private Date postponePublication;
    @Nullable
    private String place;
    private String taggedPeople;
    // region setters
    public void setNickNames(String nickNames) {
        this.taggedPeople = nickNames;
    }
    public void setLikes(int likes) {
        this.likes = likes;
    }

    public void setPlace(@Nullable String place) {
        this.place = place;
    }

    public void setPostponePublication(@Nullable Date postponePublication) {
        this.postponePublication = postponePublication;
    }

    public void setDateOfAdd(Date dateOfAdd) {
        this.dateOfAdd = dateOfAdd;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setResourceImg(@Nullable String resourceImg) {
        this.resourceImg = resourceImg;
    }

    public void setResourceVideo(@Nullable String resourceVideo) {
        this.resourceVideo = resourceVideo;
    }

    public void setMetadata(@Nullable String metadata) {
        this.metadata = metadata;
    }

    // endregion
    // region getters
    public String getNickNames() {
        return taggedPeople;
    }

    public int getLikes() {
        return likes;
    }

    @Nullable
    public String getPlace() {
        return place;
    }

    public Date getDateOfAdd() {
        return dateOfAdd;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    @Nullable
    public String getResourceImg() {
        return resourceImg;
    }

    @Nullable
    public String getResourceVideo() {
        return resourceVideo;
    }

    @Nullable
    public String getMetadata() {
        return metadata;
    }

    @Nullable
    public Date getPostponePublication() {
        return postponePublication;
    }
    // endregion

    public Post(JSONObject object) throws JSONException {
        try {
            setPostponePublication(DateFormatting.formatDateFromStandard(object.getString("postponePublication")));
            setDateOfAdd(DateFormatting.formatDateFromStandard(object.getString("addDate")));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        setMetadata(object.getString("metadata"));

        setResourceVideo(object.getString("videoUrl"));
        setResourceImg(object.getString("imageUrl"));

        setDescription(object.getString("header"));
        setPlace(object.getString("place"));
        setAuthor(object.getString("author"));
        setLikes(object.getInt("likes"));

        if (!object.isNull("taggedPeople")) {
            setNickNames(object.getString("taggedPeople"));
        }
    }

    public JSONObject crateOtherInfo() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("author","Andry"); // TODO TransitUser.user.getName()
        jsonObject.put("header", description);
        jsonObject.put("metadata", metadata);

        assert postponePublication != null;
        jsonObject.put("postponePublication", DateFormatting.formatToGeneralDate(postponePublication));
        jsonObject.put("taggedPeople", taggedPeople);

        return jsonObject;
    }

    public Post() {
    }
}
