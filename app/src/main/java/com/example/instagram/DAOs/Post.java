package com.example.instagram.DAOs;

import androidx.annotation.Nullable;

import java.util.Date;

public class Post {
    private Date dateOfAdd;
    private User author;
    private String description;
    @Nullable
    private String resourceImg;
    @Nullable
    private String resourceVideo;
    @Nullable
    private String metadata;

    // region setters
    public void setDateOfAdd(Date dateOfAdd) {
        this.dateOfAdd = dateOfAdd;
    }

    public void setAuthor(User author) {
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
    public Date getDateOfAdd() {
        return dateOfAdd;
    }

    public User getAuthor() {
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
    // endregion
}
