package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.instagram.DAOs.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Services {
    private final static String BASE_URL = "https://clickshot-374911.lm.r.appspot.com";

    // registration user
    public static void addUser(Callback<String> callback) throws IOException, JSONException {
        Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

        // create main interface
        SendToAddUser mainInterface = retrofit.create(SendToAddUser.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSON().toString());
        call.enqueue(callback);
    }

    public static void authorizeUser(Callback<String> callback) throws JSONException {
        Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

        // create main interface
        SendToCheckExistUser mainInterface = retrofit.create(SendToCheckExistUser.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONToCheck().toString());
        call.enqueue(callback);
    }

    // send login to find user
    public static void sendToForgotPassword(Callback<String> callback) throws Exception {
        Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

        // create main interface
        SendToForgotPassword mainInterface = retrofit.create(SendToForgotPassword.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONLogin().toString());
        call.enqueue(callback);
    }

    public static void sendToCheckUsedLickInMail(Callback<String> callback) throws Exception {
        Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

        // create main interface
        SendToCheckUsedLinkInMail mainInterface = retrofit.create(SendToCheckUsedLinkInMail.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getNickName());
        call.enqueue(callback);
    }

    public static void sendToCheckUserCode(Callback<String> callback, String code) {
        Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

        // create main interface
        SendToCheckUserCode mainInterface = retrofit.create(SendToCheckUserCode.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getNickName(), code);
        call.enqueue(callback);
    }

    public static void sendNewPasswordAfterForgot(Callback<String> callback) throws JSONException {
        Retrofit retrofit = MyRetrofit.initializeRetrofit(BASE_URL);

        // create main interface
        SendNewPasswordAfterForgot mainInterface = retrofit.create(SendNewPasswordAfterForgot.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(TransitUser.user.getJSONAfterForgotPassword().toString());
        call.enqueue(callback);
    }
}
