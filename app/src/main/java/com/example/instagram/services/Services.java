package com.example.instagram.services;

import android.util.Log;

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

public class Services {
    private final static String BASE_URL = "https://clickshot-374911.lm.r.appspot.com";

    private static ByteArrayOutputStream readResponse(InputStream reader) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        byte[] chunk = new byte[4096];
        int len;
        while ((len = reader.read(chunk)) != -1) {
            bytes.write(chunk, 0, len);
        }

        return bytes;
    }

    public static void addUser(User user) throws IOException, JSONException {
        URL url = new URL(Services.BASE_URL + "/add");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setDoOutput(true); // connection will be send data (send body)
        urlConnection.setDoInput(true); // connection will get data (get body)
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "*/*");
        urlConnection.setChunkedStreamingMode(0); // not fragment thread

        OutputStream body = urlConnection.getOutputStream();
        String str = user.getJSON().toString();

        body.write(str.getBytes());

        body.flush();
        body.close();
        // endregion

        int responseCode = urlConnection.getResponseCode();
        if (responseCode != 200) {
            Log.d("postChatMessage", "Response code: " + responseCode);
            return;
        }
        // endregion

        // region Response
        ByteArrayOutputStream byteArrayOutputStreamRead = readResponse(urlConnection.getInputStream());

        Log.d("addUserResponse", new String(byteArrayOutputStreamRead.toByteArray(), StandardCharsets.UTF_8));

        byteArrayOutputStreamRead.close();
        byteArrayOutputStreamRead.close();
        urlConnection.disconnect();
        // endregion
    }

    public static boolean checkExistUser(User user) throws Exception {
        URL url = new URL(Services.BASE_URL + "/authorize");
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setDoOutput(true); // connection will be send data (send body)
        urlConnection.setDoInput(true); // connection will get data (get body)
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Content-Type", "application/json");
        urlConnection.setRequestProperty("Accept", "*/*");
        urlConnection.setChunkedStreamingMode(0); // not fragment thread

        OutputStream body = urlConnection.getOutputStream();
        String str = user.getJSONToCheck().toString();
        body.write(str.getBytes());

        body.flush();
        body.close();

        int responseCode = urlConnection.getResponseCode();
        if (responseCode != 200) {
            throw new Exception("User are not authorized");
        }

        return true;
    }
}
