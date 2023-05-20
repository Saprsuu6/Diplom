package com.example.instagram.services;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class Services {
    //private final static String BASE_URL = "https://clickshot-374911.lm.r.appspot.com";
    private final static String BASE_URL = "https://c23c-62-16-20-10.ngrok-free.app";
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
        // create main interface
        SendToCheckExistUser mainInterface = retrofit.create(SendToCheckExistUser.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONToCheck().toString());
        call.enqueue(callback);
    }

    // send login to find user
    public static void sendToForgotPassword(Callback<String> callback) throws Exception {
        // create main interface
        SendToForgotPassword mainInterface = retrofit.create(SendToForgotPassword.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONLogin().toString());
        call.enqueue(callback);
    }

    public static void sendToCheckUsedLickInMail(Callback<String> callback) throws Exception {
        // create main interface
        SendToCheckUsedLinkInMail mainInterface = retrofit.create(SendToCheckUsedLinkInMail.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getNickName());
        call.enqueue(callback);
    }

    public static void sendToCheckUserCode(Callback<String> callback, String code) {
        // create main interface
        SendToCheckUserCode mainInterface = retrofit.create(SendToCheckUserCode.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getNickName(), code);
        call.enqueue(callback);
    }

    public static void sendNewPasswordAfterForgot(Callback<String> callback) throws JSONException {
        // create main interface
        SendNewPasswordAfterForgot mainInterface = retrofit.create(SendNewPasswordAfterForgot.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONAfterForgotPassword().toString());
        call.enqueue(callback);
    }

    public static void sendAva(Callback<ResponseBody> callback, RequestBody image) throws JSONException {
        // create main interface
        Multipart mainInterface = retrofit.create(Multipart.class);

        // initialize call
        Call<ResponseBody> call = mainInterface.STRING_CALL(image, null); // TODO check multipart
        call.enqueue(callback);
    }
}
