package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.TransitUser;

import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class SetAvatar extends AppCompatActivity {
    private Resources resources;
    private LinearLayout setPhoto;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_avatar);
        setUiVisibility();

        resources = getResources();
        setIntents();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());
        Localisation.setFirstLocale(languages);

        setListeners();
        setAnimations();
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setIntents() {
        if (Intents.getSetFindContacts() == null)
            Intents.setSetFindContacts(new Intent(this, FindContactsFriends.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
    }

    @Override
    protected void onResume() {
        Localisation.setFirstLocale(languages);
        super.onResume();
    }

    private void setAnimations() {
        AnimationDrawable animationDrawable = (AnimationDrawable) setPhoto.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    private void findViews() {
        setPhoto = findViewById(R.id.set_photo);
        languages = findViewById(R.id.languages);
        buttons = new Button[]{findViewById(R.id.photo_button)};
        textViews = new TextView[]{findViewById(R.id.add_photo_header),
                findViewById(R.id.add_photo_info),
                findViewById(R.id.other_variant),
                findViewById(R.id.skip),
                findViewById(R.id.reg_question),
                findViewById(R.id.link_log_in)};
    }

    @SuppressLint("ClickableViewAccessibility")
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
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        buttons[0].setOnClickListener(v -> {
            if (TransitUser.user.getOtherInfo().isMediaPermission()) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                someActivityResultLauncher.launch(i);
            } else {
                AlertDialog.Builder permissionsDialog = Permissions.getPermissionMediaDialog(this, resources);
                permissionsDialog.setNegativeButton("Cancel", null);
                permissionsDialog.create().show();
            }
        });

        textViews[5].setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Uri selectedImageUri = data.getData();
                    File ava = new File(String.valueOf(selectedImageUri));
                    if (selectedImageUri != null) {
                        System.out.println(ava);
                        // TODO find how to know extension
                    }
                }
            }
    );

    // region Localisation
    private void setStringResources() {
        buttons[0].setText(resources.getString(R.string.add_photo_btn_info));

        textViews[0].setText(resources.getString(R.string.add_photo_header));
        textViews[1].setText(resources.getString(R.string.add_photo_info));
        textViews[2].setText(resources.getString(R.string.or));
        textViews[3].setText(resources.getString(R.string.add_btn_skip));
        textViews[4].setText(resources.getString(R.string.have_an_acc));
        textViews[5].setText(resources.getString(R.string.log_in));
    }
    // endregion
}