package com.example.instagram.DAOs;

import com.example.instagram.services.DateFormatting;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

public class Notification {
    private String postId;
    private String commentId;
    private String author;
    private String user;
    private Date date;
    private String action;

    //region getters
    public String getPostId() {
        return postId;
    }

    public String getCommentId() {
        return commentId;
    }

    public String getAuthor() {
        return author;
    }

    public String getUser() {
        return user;
    }

    public Date getDate() {
        return date;
    }

    public String getAction() {
        return action;
    }

    //endregion
    //region setters
    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setAction(String action) {
        this.action = action;
    }
    //endregion

    public Notification(JSONObject notification) throws JSONException, ParseException {
        if (!notification.isNull("postId")) setPostId(notification.getString("postId"));
        if (!notification.isNull("commentId")) setCommentId(notification.getString("commentId"));
        if (!notification.isNull("author")) setAuthor(notification.getString("author"));
        if (!notification.isNull("user")) setUser(notification.getString("user"));
        if (!notification.isNull("date"))
            setDate(DateFormatting.formatDateFromStandard(notification.getString("date")));
        if (!notification.isNull("action")) setAction(notification.getString("action"));
    }
}
