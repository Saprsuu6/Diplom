package com.example.instagram.services;

import com.example.instagram.services.interfaces.SendAva;
import com.example.instagram.services.interfaces.SendMultipartPost;
import com.example.instagram.services.interfaces.SendNewPasswordAfterForgot;
import com.example.instagram.services.interfaces.SendToAddUser;
import com.example.instagram.services.interfaces.SendToCheckExistUser;
import com.example.instagram.services.interfaces.SendToCheckUsedLinkInMail;
import com.example.instagram.services.interfaces.SendToCheckUserCode;
import com.example.instagram.services.interfaces.SendToForgotPassword;
import com.example.instagram.services.interfaces.SendToGetAllPosts;
import com.example.instagram.services.interfaces.SendToGetAva;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Services {
    //private final static String BASE_URL = "https://clickshot-374911.lm.r.appspot.com";
    public final static String BASE_URL = "https://a8ea-2a09-bac5-597d-52d-00-84-8a.ngrok-free.app";
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

    public static void sendMultipartPost(Callback<ResponseBody> callback, RequestBody image, String description) throws JSONException {
        SendMultipartPost mainInterface = retrofit.create(SendMultipartPost.class);

        Call<ResponseBody> call = mainInterface.STRING_CALL(image, description);
        call.enqueue(callback);
    }

    public static void sendAva(Callback<ResponseBody> callback, RequestBody image, RequestBody login) throws JSONException {
        SendAva mainInterface = retrofit.create(SendAva.class);

        Call<ResponseBody> call = mainInterface.STRING_CALL(image, login);
        call.enqueue(callback);
    }

    public static void sendToGetAllPosts(Callback<String> callback, int amount, String scroll) throws JSONException {
        SendToGetAllPosts mainInterface = retrofit.create(SendToGetAllPosts.class);

        Call<String> call = mainInterface.STRING_CALL(amount, scroll);
        call.enqueue(callback);
    }

    public static void sendToGetAva(Callback<String> callback, String nickName) throws JSONException {
        SendToGetAva mainInterface = retrofit.create(SendToGetAva.class);

        Call<String> call = mainInterface.STRING_CALL(nickName);
        call.enqueue(callback);
    }
}
