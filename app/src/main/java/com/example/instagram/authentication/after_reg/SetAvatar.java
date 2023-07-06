package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
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
        private final TextView setAvaTitle;
        private final TextView setAvaDescription;
        private final Button openGallery;
        private final TextView or;
        private final TextView skip;
        private final TextView haveAnAccount;
        private final TextView haveAnAccountLink;
        private final Spinner languagesSpinner;

        public Views() {
            setAvaTitle = findViewById(R.id.add_photo_header);
            setAvaDescription = findViewById(R.id.add_photo_info);
            openGallery = findViewById(R.id.photo_button);
            or = findViewById(R.id.other_variant);
            skip = findViewById(R.id.skip);
            haveAnAccount = findViewById(R.id.reg_question);
            haveAnAccountLink = findViewById(R.id.link_log_in);
            languagesSpinner = findViewById(R.id.languages);
        }
    }

    private Resources resources;
    private Localisation localisation;
    private String extension;
    private byte[] imageBytes;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_avatar);

        resources = getResources();
        views = new Views();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private void setIntents() {
        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));

        Intents.setNewsList(new Intent(this, NewsLine.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
    }

    @SuppressLint({"ClickableViewAccessibility", "IntentReset"})
    private void setListeners() {
        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

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

    private void setStringResources() {
        views.openGallery.setText(resources.getString(R.string.add_photo_btn_info));
        views.setAvaTitle.setText(resources.getString(R.string.add_photo_header));
        views.setAvaDescription.setText(resources.getString(R.string.add_photo_info));
        views.or.setText(resources.getString(R.string.or));
        views.skip.setText(resources.getString(R.string.add_btn_skip));
        views.haveAnAccount.setText(resources.getString(R.string.have_an_acc));
        views.haveAnAccountLink.setText(resources.getString(R.string.log_in));
    }
}