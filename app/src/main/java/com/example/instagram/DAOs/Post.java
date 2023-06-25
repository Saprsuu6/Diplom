package com.example.instagram.DAOs;

import android.webkit.MimeTypeMap;

import androidx.annotation.Nullable;

import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.TransitUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class Post {
    public Post clone(Post post) {
        Post clone = new Post();
        clone.setPostId(post.getPostId());
        clone.setDateOfAdd(post.getDateOfAdd());
        clone.setAuthor(post.getAuthor());
        clone.setDescription(post.getDescription());
        clone.setLikes(post.getLikes());
        clone.setResourceMedia(post.getResourceMedia());

        if (metadata != null) clone.setMetadata(post.getMetadata());
        if (postponePublication != null)
            clone.setPostponePublication(post.getPostponePublication());

        clone.setTaggedPeople(post.getTaggedPeople());
        clone.setLiked(post.isLiked());
        clone.setSaved(post.getSaved());

        return clone;
    }

    private String postId;
    private Date dateOfAdd;
    private String author;
    private String description;
    private int likes;
    private String resourceMedia;
    private String metadata;
    @Nullable
    private Date postponePublication;
    private String taggedPeople;
    private Boolean isLiked = null;
    private Boolean isSaved = null;

    // region setters
    public void setTaggedPeople(String taggedPeople) {
        this.taggedPeople = taggedPeople;
    }

    public void setLiked(Boolean liked) {
        isLiked = liked;
    }

    public void setSaved(Boolean saved) {
        isSaved = saved;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setNickNames(String nickNames) {
        this.taggedPeople = nickNames;
    }

    public void setLikes(int likes) {
        this.likes = likes;
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

    public void setResourceMedia(String resourceMedia) {
        this.resourceMedia = resourceMedia;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    // endregion
    // region getters

    public String getTaggedPeople() {
        return taggedPeople;
    }

    public Boolean getLiked() {
        return isLiked;
    }

    public Boolean getSaved() {
        return isSaved;
    }

    public Boolean isLiked() {
        return isLiked;
    }

    public Boolean isSaved() {
        return isSaved;
    }

    public String getPostId() {
        return postId;
    }

    public String getNickNames() {
        return taggedPeople;
    }

    public int getLikes() {
        return likes;
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

    public String getResourceMedia() {
        return resourceMedia;
    }

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
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        setPostId(object.getString("id"));
        setMetadata(object.getString("metadata"));

        setResourceMedia(object.getString("mediaUrl"));

        setDescription(object.getString("description"));
        setAuthor(object.getString("author"));
        setLikes(object.getInt("likes"));
    }

    public JSONObject crateOtherInfo() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("author", TransitUser.user.getLogin());
        jsonObject.put("description", description);
        jsonObject.put("metadata", metadata);

        jsonObject.put("postponePublication", postponePublication != null ? DateFormatting.formatToDateWithTime(postponePublication) : DateFormatting.formatToDateWithTime(new Date()));
        jsonObject.put("taggedPeople", taggedPeople);

        return jsonObject;
    }

    public Post() {
    }

    public String getMimeType() {
        JSONObject meta;
        try {
            meta = new JSONObject(getMetadata());
            return Post.getMimeTypeFromExtension(meta.getString("Extension"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getMimeTypeFromExtension(String extension) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }
}
