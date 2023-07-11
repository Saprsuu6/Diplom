package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.MediaTypes;
import com.example.instagram.services.OpenMedia;
import com.example.instagram.services.ReadBytesForMedia;
import com.example.instagram.services.UiVisibility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class SetAvatar extends AppCompatActivity {
    private class Views {
        private final Button openGallery;
        private final TextView skip;
        private final TextView haveAnAccountLink;

        public Views() {
            openGallery = findViewById(R.id.photo_button);
            skip = findViewById(R.id.skip);
            haveAnAccountLink = findViewById(R.id.link_log_in);
        }
    }
    private String extension;
    private byte[] imageBytes;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_avatar);

        views = new Views();

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private void setIntents() {
        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));

        Intents.setNewsList(new Intent(this, NewsLine.class));
    }

    @SuppressLint({"ClickableViewAccessibility", "IntentReset"})
    private void setListeners() {
        views.openGallery.setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.IMAGE, someActivityResultLauncher));

        views.haveAnAccountLink.setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
        });

        views.skip.setOnClickListener(v -> {
            Cache.saveSP(this, CacheScopes.USER_AVA.toString(), "");
            startActivity(Intents.getNewsList());
            finish();
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();

            if (data != null) {
                Uri selectedImageUri = data.getData();

                extension = FindExtension.getExtension(selectedImageUri, getApplicationContext());

                try {
                    imageBytes = ReadBytesForMedia.readBytes(this, selectedImageUri);

                    if (imageBytes == null) {
                        Toast.makeText(this, getString(R.string.tiramisu_or_better), Toast.LENGTH_SHORT).show();
                    }

                    sendAvaToBackEnd();
                } catch (IOException | JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    });

    private void sendAvaToBackEnd() throws JSONException {
        if (imageBytes.length == 0) {
            Toast.makeText(getApplicationContext(), R.string.error_no_photo, Toast.LENGTH_SHORT).show();
            return;
        }

        String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
        JSONObject jsonObject = new JSONObject().put("login", login);
        RequestBody image = RequestBody.create(MediaType.parse(getString(R.string.mime_image) + "/" + extension), imageBytes);

        // send avatar
        new DoCallBack().setValues(null, this, new Object[]{image, jsonObject}).sendAva();

        startActivity(Intents.getNewsList());
        finish();
    }
}