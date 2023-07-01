package com.example.instagram.services;

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
import com.example.instagram.services.interfaces.SendToGetAllPosts;
import com.example.instagram.services.interfaces.SendToGetAva;
import com.example.instagram.services.interfaces.SendToGetCurrentUser;
import com.example.instagram.services.interfaces.SendToGetIsLiked;
import com.example.instagram.services.interfaces.SendToGetIsSaved;
import com.example.instagram.services.interfaces.SendToGetPostsOfUser;
import com.example.instagram.services.interfaces.SendToGetTaggedPeople;
import com.example.instagram.services.interfaces.SendToLikeUnlikePost;
import com.example.instagram.services.interfaces.SendToSaveUnsavedPost;
import com.example.instagram.services.interfaces.SendToSendCodeToEmail;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Services {
    //private final static String BASE_URL = "https://clickshot-374911.lm.r.appspot.com";
    public final static String BASE_URL = "https://6bce-2a09-bac5-597b-52d-00-84-8f.ngrok-free.app";
    private final static Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

    public static void addUser(Callback<String> callback) throws IOException, JSONException {
        // create main interface
        SendToAddUser mainInterface = retrofit.create(SendToAddUser.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSON().toString());
        call.enqueue(callback);
    }

    public static void authorizeUser(Callback<String> callback) throws JSONException {
        SendToCheckExistUser mainInterface = retrofit.create(SendToCheckExistUser.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONToCheck().toString());
        call.enqueue(callback);
    }

    public static void sendToForgotPassword(Callback<String> callback) throws JSONException {
        SendToSendCodeToEmail mainInterface = retrofit.create(SendToSendCodeToEmail.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONLogin().toString());
        call.enqueue(callback);
    }

    public static void sendToCheckUsedLickInMail(Callback<String> callback) throws JSONException {
        SendToCheckUsedLinkInMail mainInterface = retrofit.create(SendToCheckUsedLinkInMail.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getLogin());
        call.enqueue(callback);
    }

    public static void sendToCheckUserCode(Callback<String> callback, String code) {
        SendToCheckUserCode mainInterface = retrofit.create(SendToCheckUserCode.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getLogin(), code);
        call.enqueue(callback);
    }

    public static void sendNewPasswordAfterForgot(Callback<String> callback) throws JSONException {
        SendNewPasswordAfterForgot mainInterface = retrofit.create(SendNewPasswordAfterForgot.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONAfterForgotPassword().toString());
        call.enqueue(callback);
    }

    public static void sendMultipartPost(Callback<ResponseBody> callback, RequestBody image, String description) throws JSONException {
        SendToAddPost mainInterface = retrofit.create(SendToAddPost.class);

        Call<ResponseBody> call = mainInterface.STRING_CALL(image, description);
        call.enqueue(callback);
    }

    public static void sendAva(Callback<ResponseBody> callback, RequestBody image, RequestBody login) throws JSONException {
        SendAva mainInterface = retrofit.create(SendAva.class);

        Call<ResponseBody> call = mainInterface.STRING_CALL(image, login);
        call.enqueue(callback);
    }

    public static void sendToGetAllPosts(Callback<String> callback, int from, int amount) {
        SendToGetAllPosts mainInterface = retrofit.create(SendToGetAllPosts.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount);
        call.enqueue(callback);
    }

    public static void sendToGetAva(Callback<String> callback, String nickName) throws JSONException {
        SendToGetAva mainInterface = retrofit.create(SendToGetAva.class);

        Call<String> call = mainInterface.STRING_CALL(nickName);
        call.enqueue(callback);
    }

    public static void sendToGetTaggedPeople(Callback<String> callback, String postId) throws JSONException {
        SendToGetTaggedPeople mainInterface = retrofit.create(SendToGetTaggedPeople.class);

        Call<String> call = mainInterface.STRING_CALL(postId);
        call.enqueue(callback);
    }

    public static void sendToDeletePost(Callback<String> callback, String postId) throws JSONException {
        SendToDeletePost mainInterface = retrofit.create(SendToDeletePost.class);

        Call<String> call = mainInterface.STRING_CALL(postId);
        call.enqueue(callback);
    }

    public static void sendToLikeUnlikePost(Callback<String> callback, String body) throws JSONException {
        SendToLikeUnlikePost mainInterface = retrofit.create(SendToLikeUnlikePost.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetIsLiked(Callback<String> callback, String postId, String login) throws JSONException {
        SendToGetIsLiked mainInterface = retrofit.create(SendToGetIsLiked.class);

        Call<String> call = mainInterface.STRING_CALL(postId, login);
        call.enqueue(callback);
    }

    public static void sendToSaveUnsavedPost(Callback<String> callback, String body) throws JSONException {
        SendToSaveUnsavedPost mainInterface = retrofit.create(SendToSaveUnsavedPost.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetIsSaved(Callback<String> callback, String postId, String login) throws JSONException {
        SendToGetIsSaved mainInterface = retrofit.create(SendToGetIsSaved.class);

        Call<String> call = mainInterface.STRING_CALL(postId, login);
        call.enqueue(callback);
    }

    public static void sendToAddNewComment(Callback<String> callback, String comment) throws JSONException {
        SendToAddNewComment mainInterface = retrofit.create(SendToAddNewComment.class);

        Call<String> call = mainInterface.STRING_CALL(comment);
        call.enqueue(callback);
    }

    public static void sendToGetAllComments(Callback<String> callback, int from, int amount, String postId) {
        SendToGetAllComments mainInterface = retrofit.create(SendToGetAllComments.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount, postId);
        call.enqueue(callback);
    }

    public static void sendToDeleteComment(Callback<String> callback, String body) throws JSONException {
        SendToDeleteComment mainInterface = retrofit.create(SendToDeleteComment.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToAddNewAnswer(Callback<String> callback, String body) throws JSONException {
        SendToAddNewAnswer mainInterface = retrofit.create(SendToAddNewAnswer.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToChangeComment(Callback<String> callback, String body) throws JSONException {
        SendToChangeComment mainInterface = retrofit.create(SendToChangeComment.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetPostsOfUser(Callback<String> callback, int from, int amount, String login) {
        SendToGetPostsOfUser mainInterface = retrofit.create(SendToGetPostsOfUser.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount, login);
        call.enqueue(callback);
    }

    public static void sendToGetCurrentUser(Callback<String> callback, String login) throws JSONException {
        SendToGetCurrentUser mainInterface = retrofit.create(SendToGetCurrentUser.class);

        Call<String> call = mainInterface.STRING_CALL(login);
        call.enqueue(callback);
    }

    public static void sendToChangeUser(Callback<String> callback, String body) throws JSONException {
        SendToChangeUser mainInterface = retrofit.create(SendToChangeUser.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToSendCodeForEmail(Callback<String> callback, String body) throws JSONException {
        SendToSendCodeToEmail mainInterface = retrofit.create(SendToSendCodeToEmail.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToChangeEmailFinally(Callback<String> callback, String body) throws JSONException {
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
}
