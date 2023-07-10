package com.example.instagram.services.interfaces;

import com.example.instagram.DAOs.Post;

import org.json.JSONException;

import java.io.IOException;

public interface CallBack {
    void deletePost() throws JSONException;

    void likeUnlikePost() throws JSONException;

    void logIn() throws JSONException;

    void sinUp() throws JSONException, IOException;

    void sendToCodeToRestorePassword() throws JSONException;

    void sendToCheckUsedLickInMailPassword() throws JSONException;

    void sendToCheckUsedLickInMailEmail() throws JSONException;

    void sendToCheckUserCode() throws JSONException;

    void sendNewPasswordAfterForgot() throws JSONException;

    void sendMultipartPost() throws JSONException;

    void sendAva() throws JSONException;

    void sendToGetAvaImage() throws JSONException;

    void sendToGetAvaInTagPeople() throws JSONException;

    void sendToGetCurrentUser() throws JSONException;

    void sendToGetTaggedPeople() throws JSONException;

    void sendToGetIsLikedForDialogPost() throws JSONException;

    void sendToSaveUnsavedPost() throws JSONException;

    void sendToGetIsSaved() throws JSONException;

    void sendToAddNewComment() throws JSONException;

    void sendToAddNewAnswer() throws JSONException;

    void sendToGetAllComments() throws JSONException;

    void sendToDeleteComment() throws JSONException;

    void sendToChangeComment() throws JSONException;

    void sendToGetPostsOfUserInCells() throws JSONException;

    void sendToChangeUser() throws JSONException;

    void sendToSendCodeForChangeEmail() throws JSONException;

    void sendToChangeEmailFinally() throws JSONException;

    void sendToFindUser() throws JSONException;

    void sendToFindPost() throws JSONException;

    void sendToGetIsMeSubscribed() throws JSONException;

    void sendToSetStateSubscribe() throws JSONException;

    void sendToGetSubscribers() throws JSONException;

    void sendToGetSubscribing() throws JSONException;

    void sendToGetNotifications() throws JSONException;

    void sendToGetPostById() throws JSONException;

    void sendToGetCommentById() throws JSONException;
    void getSavedPosts() throws JSONException;
}
