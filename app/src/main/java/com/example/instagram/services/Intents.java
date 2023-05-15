package com.example.instagram.services;

import android.content.Intent;

public class Intents {
    private static Intent createNewPassword;
    private static Intent authorisation;
    private static Intent registration;
    private static Intent forgotPassword;
    private static Intent setName;    //region getters
    private static Intent setPassword;
    private static Intent setBirthday;
    private static Intent setAvatar;
    private static Intent setFindContacts;
    private static Intent newsList;
    private static Intent comments;
    private static Intent selfPage;
    private static Intent chatList;

    //region getters
    public static Intent getCreateNewPassword() {
        return createNewPassword;
    }

    public static Intent getChatList() {
        return chatList;
    }

    public static Intent getSelfPage() {
        return selfPage;
    }

    public static Intent getComments() {
        return comments;
    }

    public static Intent getSetFindContacts() {
        return setFindContacts;
    }

    public static Intent getSetAvatar() {
        return setAvatar;
    }

    public static Intent getSetName() {
        return setName;
    }

    public static Intent getSetPassword() {
        return setPassword;
    }

    public static Intent getSetBirthday() {
        return setBirthday;
    }

    public static Intent getAuthorisation() {
        return authorisation;
    }

    public static Intent getRegistration() {
        return registration;
    }

    public static Intent getForgotPassword() {
        return forgotPassword;
    }

    public static Intent getNewsList() {
        return newsList;
    }

    //endregion
    // region setters
    public static void setCreateNewPassword(Intent createNewPassword) {
        Intents.createNewPassword = createNewPassword;
    }
    public static void setChatList(Intent chatList) {
        Intents.chatList = chatList;
    }

    public static void setSelfPage(Intent selfPage) {
        Intents.selfPage = selfPage;
    }

    public static void setComments(Intent comments) {
        Intents.comments = comments;
    }

    public static void setNewsList(Intent newsList) {
        Intents.newsList = newsList;
    }

    public static void setSetFindContacts(Intent setFindContacts) {
        Intents.setFindContacts = setFindContacts;
    }

    public static void setSetAvatar(Intent setAvatar) {
        Intents.setAvatar = setAvatar;
    }

    public static void setAuthorisation(Intent authorisation) {
        Intents.authorisation = authorisation;
    }

    public static void setRegistration(Intent registration) {
        Intents.registration = registration;
    }

    public static void setForgotPassword(Intent forgotPassword) {
        Intents.forgotPassword = forgotPassword;
    }

    public static void setSetName(Intent setName) {
        Intents.setName = setName;
    }

    public static void setSetPassword(Intent setPassword) {
        Intents.setPassword = setPassword;
    }

    public static void setSetBirthday(Intent setBirthday) {
        Intents.setBirthday = setBirthday;
    }
    //endregion
}
