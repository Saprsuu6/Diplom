package com.example.instagram.DAOs;

import com.example.instagram.main_process.Comments;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.TransitUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.Map;

public class Comment {
    private boolean toChange;
    private String replayedCommentId;
    private String commentId;
    private Date dateOfAdd;
    private String postId;
    private String author;
    private String content;

    public Comment(JSONObject jsonObject) throws JSONException, ParseException {
        if (!jsonObject.isNull("commentId")) {
            replayedCommentId = jsonObject.getString("commentId");

            JSONObject answer = jsonObject.getJSONObject("answerComment");
            parseComment(answer);
        } else {
            parseComment(jsonObject);
        }
    }

    private void parseComment(JSONObject jsonObject) throws JSONException, ParseException {
        commentId = jsonObject.getString("id");
        author = jsonObject.getString("author");
        dateOfAdd = DateFormatting.formatDateFromStandard(jsonObject.getString("date"));
        content = jsonObject.getString("content");

        if (!jsonObject.isNull("postId")) {
            postId = jsonObject.getString("postId");
        }
    }

    public Comment() {

    }

    public JSONObject getJSONComment() throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("postId", postId);
        jsonObject.put("date", DateFormatting.formatToDateWithTime(new Date()));
        jsonObject.put("author", TransitUser.user.getLogin());
        jsonObject.put("content", content);

        return jsonObject;
    }

    public JSONObject getJSONCommentToChange(String text) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        jsonObject.put("id", commentId);
        jsonObject.put("postId", postId);
        jsonObject.put("date", DateFormatting.formatToDateWithTime(new Date()));
        jsonObject.put("author", TransitUser.user.getLogin());
        jsonObject.put("content", text);

        return jsonObject;
    }

    public JSONObject getJSONReply() throws JSONException {
        JSONObject jsonObjectComment = getJSONComment();

        JSONObject jsonObjectReplay = new JSONObject();
        jsonObjectReplay.put("commentId", replayedCommentId);
        jsonObjectReplay.put("answer", jsonObjectComment);

        return jsonObjectReplay;
    }

    //region setters
    public void setToChange(boolean toChange) {
        this.toChange = toChange;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public void setReplayedCommentId(String replayedCommentId) {
        this.replayedCommentId = replayedCommentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public void setDateOfAdd(Date dateOfAdd) {
        this.dateOfAdd = dateOfAdd;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setContent(String content) {
        this.content = content;
    }

    // endregion
    // region getters
    public String getPostId() {
        return postId;
    }

    public boolean isToChange() {
        return toChange;
    }

    public String getReplayedCommentId() {
        return replayedCommentId;
    }

    public String getCommentId() {
        return commentId;
    }

    public Date getDateOfAdd() {
        return dateOfAdd;
    }

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }
    //endregion
}
