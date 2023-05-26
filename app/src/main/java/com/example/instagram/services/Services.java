package com.example.instagram.services;

import com.example.instagram.services.pagination.PagingRequestPages;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Services {
    //private final static String BASE_URL = "https://clickshot-374911.lm.r.appspot.com";
    private final static String BASE_URL = "https://1a9a-62-16-0-223.ngrok-free.app";
    private final static Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

    // registration user
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

    // send login to find user
    public static void sendToForgotPassword(Callback<String> callback) throws Exception {
        SendToForgotPassword mainInterface = retrofit.create(SendToForgotPassword.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONLogin().toString());
        call.enqueue(callback);
    }

    public static void sendToCheckUsedLickInMail(Callback<String> callback) throws Exception {
        SendToCheckUsedLinkInMail mainInterface = retrofit.create(SendToCheckUsedLinkInMail.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getNickName());
        call.enqueue(callback);
    }

    public static void sendToCheckUserCode(Callback<String> callback, String code) {
        SendToCheckUserCode mainInterface = retrofit.create(SendToCheckUserCode.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getNickName(), code);
        call.enqueue(callback);
    }

    public static void sendNewPasswordAfterForgot(Callback<String> callback) throws JSONException {
        SendNewPasswordAfterForgot mainInterface = retrofit.create(SendNewPasswordAfterForgot.class);

        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONAfterForgotPassword().toString());
        call.enqueue(callback);
    }

    public static void sendAva(Callback<ResponseBody> callback, RequestBody image) throws JSONException {
        Multipart mainInterface = retrofit.create(Multipart.class);

        Call<ResponseBody> call = mainInterface.STRING_CALL(image, null);
        call.enqueue(callback);
    }

    public static void sendToGetPosts(Callback<String> callback, int page, int onePageLimit) throws JSONException { // TODO change method
        String link = "https://picsum.photos"; // TODO change way
        Retrofit retrofit = MyRetrofit.initializeRetrofit(link);

        PagingRequestPages mainInterface = retrofit.create(PagingRequestPages.class);
        Call<String> call = mainInterface.STRING_CALL(page, onePageLimit);

        call.enqueue(callback);
    }

    public static void sendToGetUsers(Callback<String> callback, int page, int onePageLimit) throws JSONException { // TODO change method
        String link = "https://picsum.photos"; // TODO change way
        Retrofit retrofit = MyRetrofit.initializeRetrofit(link);

        PagingRequestPages mainInterface = retrofit.create(PagingRequestPages.class);
        Call<String> call = mainInterface.STRING_CALL(page, onePageLimit);

        call.enqueue(callback);
    }
}
