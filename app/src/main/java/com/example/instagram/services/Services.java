package com.example.instagram.services;

import com.example.instagram.services.interfaces.AddNewAnswer;
import com.example.instagram.services.interfaces.AddNewComment;
import com.example.instagram.services.interfaces.AddPost;
import com.example.instagram.services.interfaces.AddUser;
import com.example.instagram.services.interfaces.ChangeComment;
import com.example.instagram.services.interfaces.ChangeEmailFinally;
import com.example.instagram.services.interfaces.ChangeUser;
import com.example.instagram.services.interfaces.CheckExistUser;
import com.example.instagram.services.interfaces.CheckUsedLinkInMail;
import com.example.instagram.services.interfaces.CheckUserCode;
import com.example.instagram.services.interfaces.DeleteComment;
import com.example.instagram.services.interfaces.DeletePost;
import com.example.instagram.services.interfaces.FindPost;
import com.example.instagram.services.interfaces.FindUser;
import com.example.instagram.services.interfaces.GetAllComments;
import com.example.instagram.services.interfaces.GetAva;
import com.example.instagram.services.interfaces.GetCommentById;
import com.example.instagram.services.interfaces.GetIsLiked;
import com.example.instagram.services.interfaces.GetIsMeSubscribed;
import com.example.instagram.services.interfaces.GetIsSaved;
import com.example.instagram.services.interfaces.GetNotifications;
import com.example.instagram.services.interfaces.GetPostById;
import com.example.instagram.services.interfaces.GetPostsOfUser;
import com.example.instagram.services.interfaces.GetPublicUser;
import com.example.instagram.services.interfaces.GetSavedPosts;
import com.example.instagram.services.interfaces.GetSubscribers;
import com.example.instagram.services.interfaces.GetSubscribing;
import com.example.instagram.services.interfaces.GetTaggedPeople;
import com.example.instagram.services.interfaces.NewAva;
import com.example.instagram.services.interfaces.NewPasswordAfterForgot;
import com.example.instagram.services.interfaces.SendCodeToEmail;
import com.example.instagram.services.interfaces.SetStateOfLikePost;
import com.example.instagram.services.interfaces.SetStateOfSavePost;
import com.example.instagram.services.interfaces.SetStateSubscribe;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Services {
    public static String BASE_URL;
    public static Retrofit retrofit;

    public static void addUser(Callback<String> callback, String body) {
        // create main interface
        AddUser mainInterface = retrofit.create(AddUser.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void authorizeUser(Callback<String> callback, String body) {
        CheckExistUser mainInterface = retrofit.create(CheckExistUser.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToForgotPassword(Callback<String> callback, String body) {
        SendCodeToEmail mainInterface = retrofit.create(SendCodeToEmail.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToCheckUsedLickInMail(Callback<String> callback, String login) {
        CheckUsedLinkInMail mainInterface = retrofit.create(CheckUsedLinkInMail.class);

        Call<String> call = mainInterface.STRING_CALL(login);
        call.enqueue(callback);
    }

    public static void sendToCheckUserCode(Callback<String> callback, String login, String code) {
        CheckUserCode mainInterface = retrofit.create(CheckUserCode.class);

        Call<String> call = mainInterface.STRING_CALL(login, code);
        call.enqueue(callback);
    }

    public static void sendNewPasswordAfterForgot(Callback<String> callback, String body) {
        NewPasswordAfterForgot mainInterface = retrofit.create(NewPasswordAfterForgot.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendMultipartPost(Callback<String> callback, RequestBody image, String body) {
        AddPost mainInterface = retrofit.create(AddPost.class);

        Call<String> call = mainInterface.STRING_CALL(image, body);
        call.enqueue(callback);
    }

    public static void sendAva(Callback<String> callback, RequestBody image, String login) {
        NewAva mainInterface = retrofit.create(NewAva.class);

        Call<String> call = mainInterface.STRING_CALL(image, login);
        call.enqueue(callback);
    }

    public static void sendToGetAva(Callback<String> callback, String nickName) {
        GetAva mainInterface = retrofit.create(GetAva.class);

        Call<String> call = mainInterface.STRING_CALL(nickName);
        call.enqueue(callback);
    }

    public static void sendToGetTaggedPeople(Callback<String> callback, String postId) {
        GetTaggedPeople mainInterface = retrofit.create(GetTaggedPeople.class);

        Call<String> call = mainInterface.STRING_CALL(postId);
        call.enqueue(callback);
    }

    public static void sendToDeletePost(Callback<String> callback, String postId) {
        DeletePost mainInterface = retrofit.create(DeletePost.class);

        Call<String> call = mainInterface.STRING_CALL(postId);
        call.enqueue(callback);
    }

    public static void sendToLikeUnlikePost(Callback<String> callback, String body) {
        SetStateOfLikePost mainInterface = retrofit.create(SetStateOfLikePost.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetIsLiked(Callback<String> callback, String postId, String login) {
        GetIsLiked mainInterface = retrofit.create(GetIsLiked.class);

        Call<String> call = mainInterface.STRING_CALL(postId, login);
        call.enqueue(callback);
    }

    public static void sendToSaveUnsavedPost(Callback<String> callback, String body) {
        SetStateOfSavePost mainInterface = retrofit.create(SetStateOfSavePost.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetIsSaved(Callback<String> callback, String postId, String login) {
        GetIsSaved mainInterface = retrofit.create(GetIsSaved.class);

        Call<String> call = mainInterface.STRING_CALL(postId, login);
        call.enqueue(callback);
    }

    public static void sendToAddNewComment(Callback<String> callback, String comment) {
        AddNewComment mainInterface = retrofit.create(AddNewComment.class);

        Call<String> call = mainInterface.STRING_CALL(comment);
        call.enqueue(callback);
    }

    public static void sendToGetAllComments(Callback<String> callback, int from, int amount, String postId) {
        GetAllComments mainInterface = retrofit.create(GetAllComments.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount, postId);
        call.enqueue(callback);
    }

    public static void sendToDeleteComment(Callback<String> callback, String body) {
        DeleteComment mainInterface = retrofit.create(DeleteComment.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToAddNewAnswer(Callback<String> callback, String body) {
        AddNewAnswer mainInterface = retrofit.create(AddNewAnswer.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToChangeComment(Callback<String> callback, String body) {
        ChangeComment mainInterface = retrofit.create(ChangeComment.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetPostsOfUser(Callback<String> callback, int from, int amount, String login) {
        GetPostsOfUser mainInterface = retrofit.create(GetPostsOfUser.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount, login);
        call.enqueue(callback);
    }

    public static void sendToGetCurrentUser(Callback<String> callback, String login) {
        GetPublicUser mainInterface = retrofit.create(GetPublicUser.class);

        Call<String> call = mainInterface.STRING_CALL(login);
        call.enqueue(callback);
    }

    public static void sendToChangeUser(Callback<String> callback, String body) {
        ChangeUser mainInterface = retrofit.create(ChangeUser.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToSendCodeForEmail(Callback<String> callback, String body) {
        SendCodeToEmail mainInterface = retrofit.create(SendCodeToEmail.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToChangeEmailFinally(Callback<String> callback, String body) {
        ChangeEmailFinally mainInterface = retrofit.create(ChangeEmailFinally.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToFindUser(Callback<String> callback, String body) {
        FindUser mainInterface = retrofit.create(FindUser.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToFindPost(Callback<String> callback, String body) {
        FindPost mainInterface = retrofit.create(FindPost.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetIsMeSubscribed(Callback<String> callback, String body) {
        GetIsMeSubscribed mainInterface = retrofit.create(GetIsMeSubscribed.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToSetStateOfSubscribe(Callback<String> callback, String body) {
        SetStateSubscribe mainInterface = retrofit.create(SetStateSubscribe.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetSubscribers(Callback<String> callback, String body) {
        GetSubscribers mainInterface = retrofit.create(GetSubscribers.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetSubscribing(Callback<String> callback, String body) {
        GetSubscribing mainInterface = retrofit.create(GetSubscribing.class);

        Call<String> call = mainInterface.STRING_CALL(body);
        call.enqueue(callback);
    }

    public static void sendToGetNotifications(Callback<String> callback, String login, String from, String amount) {
        GetNotifications mainInterface = retrofit.create(GetNotifications.class);

        Call<String> call = mainInterface.STRING_CALL(login, from, amount);
        call.enqueue(callback);
    }

    public static void sendToGetPostById(Callback<String> callback, String postId) {
        GetPostById mainInterface = retrofit.create(GetPostById.class);

        Call<String> call = mainInterface.STRING_CALL(postId);
        call.enqueue(callback);
    }

    public static void sendToGetCommentById(Callback<String> callback, String commentId) {
        GetCommentById mainInterface = retrofit.create(GetCommentById.class);

        Call<String> call = mainInterface.STRING_CALL(commentId);
        call.enqueue(callback);
    }

    public static void getSavedPosts(Callback<String> callback, String from, String amount, String login) {
        GetSavedPosts mainInterface = retrofit.create(GetSavedPosts.class);

        Call<String> call = mainInterface.STRING_CALL(from, amount, login);
        call.enqueue(callback);
    }
}
