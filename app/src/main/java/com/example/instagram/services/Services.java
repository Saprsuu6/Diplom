package com.example.instagram.services;

import com.example.instagram.services.interfaces.GetSavedPosts;
import com.example.instagram.services.interfaces.SendAva;
import com.example.instagram.services.interfaces.SendNewPasswordAfterForgot;
import com.example.instagram.services.interfaces.SendToAddNewAnswer;
import com.example.instagram.services.interfaces.SendToAddNewComment;
import com.example.instagram.services.interfaces.SendToAddPost;
import com.example.instagram.services.interfaces.SendToAddUser;
import com.example.instagram.services.interfaces.SendToChangeComment;
import com.example.instagram.services.interfaces.SendToChangeEmailFinally;
import com.example.instagram.services.interfaces.SendToChangeUser;
import com.example.instagram.services.interfaces.SendToCheckExistUser;
import com.example.instagram.services.interfaces.SendToCheckUsedLinkInMail;
import com.example.instagram.services.interfaces.SendToCheckUserCode;
import com.example.instagram.services.interfaces.SendToDeleteComment;
import com.example.instagram.services.interfaces.SendToDeletePost;
import com.example.instagram.services.interfaces.SendToFindPost;
import com.example.instagram.services.interfaces.SendToFindUser;
import com.example.instagram.services.interfaces.SendToGetAllComments;
import com.example.instagram.services.interfaces.SendToGetAva;
import com.example.instagram.services.interfaces.SendToGetCommentById;
import com.example.instagram.services.interfaces.GetPublicUser;
import com.example.instagram.services.interfaces.SendToGetIsLiked;
import com.example.instagram.services.interfaces.SendToGetIsMeSubscribed;
import com.example.instagram.services.interfaces.SendToGetIsSaved;
import com.example.instagram.services.interfaces.SendToGetNotifications;
import com.example.instagram.services.interfaces.SendToGetPostById;
import com.example.instagram.services.interfaces.SendToGetPostsOfUser;
import com.example.instagram.services.interfaces.SendToGetSubscribers;
import com.example.instagram.services.interfaces.SendToGetSubscribing;
import com.example.instagram.services.interfaces.SendToGetTaggedPeople;
import com.example.instagram.services.interfaces.SendToSendCodeToEmail;
import com.example.instagram.services.interfaces.SendToSetStateOfSavePost;
import com.example.instagram.services.interfaces.SendToSetStateSubscribe;
import com.example.instagram.services.interfaces.SendToSteStateOfLikePost;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Services {
    // TODO set link to tom cat
    // TODO add links to web
    // link to server /Clickshot/some path/app/profile/Clickshot/getPublicUser/?login=LOGIN_OF_USER
    // link to server /Clickshot/some path/app/components/Post/Clickshot/getPublicUser/?login=POST_ID
    public final static String BASE_URL = "https://668d-2a09-bac5-597d-52d-00-84-66.ngrok-free.app/";
    private final static Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

    public static void addUser(Callback<String> callback, String body) {
        // create main interface
        SendToAddUser mainInterface = retrofit.create(SendToAddUser.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void authorizeUser(Callback<String> callback, String body) {
        SendToCheckExistUser mainInterface = retrofit.create(SendToCheckExistUser.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToForgotPassword(Callback<String> callback, String body) {
        SendToSendCodeToEmail mainInterface = retrofit.create(SendToSendCodeToEmail.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToCheckUsedLickInMail(Callback<String> callback, String login) {
        SendToCheckUsedLinkInMail mainInterface = retrofit.create(SendToCheckUsedLinkInMail.class);

        Call<String> call = mainInterface.STRING_CALL(login);
        call.enqueue(callback);
    }

    public static void sendToCheckUserCode(Callback<String> callback, String login, String code) {
        SendToCheckUserCode mainInterface = retrofit.create(SendToCheckUserCode.class);

        Call<String> call = mainInterface.STRING_CALL(login, code);
        call.enqueue(callback);
    }

    public static void sendNewPasswordAfterForgot(Callback<String> callback, String body) {
        SendNewPasswordAfterForgot mainInterface = retrofit.create(SendNewPasswordAfterForgot.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendMultipartPost(Callback<String> callback, RequestBody image, String body) {
        SendToAddPost mainInterface = retrofit.create(SendToAddPost.class);

        Call<String> call = mainInterface.STRING_CALL(image, body);
        call.enqueue(callback);
    }

    public static void sendAva(Callback<String> callback, RequestBody image, String login) {
        SendAva mainInterface = retrofit.create(SendAva.class);

        Call<String> call = mainInterface.STRING_CALL(image, login);
        call.enqueue(callback);
    }

    public static void sendToGetAva(Callback<String> callback, String nickName) {
        SendToGetAva mainInterface = retrofit.create(SendToGetAva.class);

        Call<String> call = mainInterface.STRING_CALL(nickName);
        call.enqueue(callback);
    }

    public static void sendToGetTaggedPeople(Callback<String> callback, String postId) {
        SendToGetTaggedPeople mainInterface = retrofit.create(SendToGetTaggedPeople.class);

        Call<String> call = mainInterface.STRING_CALL(postId);
        call.enqueue(callback);
    }

    public static void sendToDeletePost(Callback<String> callback, String postId) {
        SendToDeletePost mainInterface = retrofit.create(SendToDeletePost.class);

        Call<String> call = mainInterface.STRING_CALL(postId);
        call.enqueue(callback);
    }

    public static void sendToLikeUnlikePost(Callback<String> callback, String body) {
        SendToSteStateOfLikePost mainInterface = retrofit.create(SendToSteStateOfLikePost.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetIsLiked(Callback<String> callback, String postId, String login) {
        SendToGetIsLiked mainInterface = retrofit.create(SendToGetIsLiked.class);

        Call<String> call = mainInterface.STRING_CALL(postId, login);
        call.enqueue(callback);
    }

    public static void sendToSaveUnsavedPost(Callback<String> callback, String body) {
        SendToSetStateOfSavePost mainInterface = retrofit.create(SendToSetStateOfSavePost.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetIsSaved(Callback<String> callback, String postId, String login) {
        SendToGetIsSaved mainInterface = retrofit.create(SendToGetIsSaved.class);

        Call<String> call = mainInterface.STRING_CALL(postId, login);
        call.enqueue(callback);
    }

    public static void sendToAddNewComment(Callback<String> callback, String comment) {
        SendToAddNewComment mainInterface = retrofit.create(SendToAddNewComment.class);

        Call<String> call = mainInterface.STRING_CALL(comment);
        call.enqueue(callback);
    }

    public static void sendToGetAllComments(Callback<String> callback, int from, int amount, String postId) {
        SendToGetAllComments mainInterface = retrofit.create(SendToGetAllComments.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount, postId);
        call.enqueue(callback);
    }

    public static void sendToDeleteComment(Callback<String> callback, String body) {
        SendToDeleteComment mainInterface = retrofit.create(SendToDeleteComment.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToAddNewAnswer(Callback<String> callback, String body) {
        SendToAddNewAnswer mainInterface = retrofit.create(SendToAddNewAnswer.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToChangeComment(Callback<String> callback, String body) {
        SendToChangeComment mainInterface = retrofit.create(SendToChangeComment.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetPostsOfUser(Callback<String> callback, int from, int amount, String login) {
        SendToGetPostsOfUser mainInterface = retrofit.create(SendToGetPostsOfUser.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount, login);
        call.enqueue(callback);
    }

    public static void sendToGetCurrentUser(Callback<String> callback, String login) {
        GetPublicUser mainInterface = retrofit.create(GetPublicUser.class);

        Call<String> call = mainInterface.STRING_CALL(login);
        call.enqueue(callback);
    }

    public static void sendToChangeUser(Callback<String> callback, String body) {
        SendToChangeUser mainInterface = retrofit.create(SendToChangeUser.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToSendCodeForEmail(Callback<String> callback, String body) {
        SendToSendCodeToEmail mainInterface = retrofit.create(SendToSendCodeToEmail.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToChangeEmailFinally(Callback<String> callback, String body) {
        SendToChangeEmailFinally mainInterface = retrofit.create(SendToChangeEmailFinally.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToFindUser(Callback<String> callback, String body) {
        SendToFindUser mainInterface = retrofit.create(SendToFindUser.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToFindPost(Callback<String> callback, String body) {
        SendToFindPost mainInterface = retrofit.create(SendToFindPost.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetIsMeSubscribed(Callback<String> callback, String body) {
        SendToGetIsMeSubscribed mainInterface = retrofit.create(SendToGetIsMeSubscribed.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToSetStateOfSubscribe(Callback<String> callback, String body) {
        SendToSetStateSubscribe mainInterface = retrofit.create(SendToSetStateSubscribe.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetSubscribers(Callback<String> callback, String body) {
        SendToGetSubscribers mainInterface = retrofit.create(SendToGetSubscribers.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetSubscribing(Callback<String> callback, String body) {
        SendToGetSubscribing mainInterface = retrofit.create(SendToGetSubscribing.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetNotifications(Callback<String> callback, String login, String from, String amount) {
        SendToGetNotifications mainInterface = retrofit.create(SendToGetNotifications.class);

        Call<String> call = mainInterface.STRING_CALL(login, from, amount);
        call.enqueue(callback);
    }

    public static void sendToGetPostById(Callback<String> callback, String postId) {
        SendToGetPostById mainInterface = retrofit.create(SendToGetPostById.class);

        Call<String> call = mainInterface.STRING_CALL(postId);
        call.enqueue(callback);
    }

    public static void sendToGetCommentById(Callback<String> callback, String commentId) {
        SendToGetCommentById mainInterface = retrofit.create(SendToGetCommentById.class);

        Call<String> call = mainInterface.STRING_CALL(commentId);
        call.enqueue(callback);
    }

    public static void getSavedPosts(Callback<String> callback, String from, String amount, String login) {
        GetSavedPosts mainInterface = retrofit.create(GetSavedPosts.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount, login);
        call.enqueue(callback);
    }
}
