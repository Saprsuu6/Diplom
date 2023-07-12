package com.example.instagram.authentication.after_reg;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.Intents;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.Resources;
import com.example.instagram.services.UiVisibility;

public class SetName extends AppCompatActivity {
    private class Views {
        private final EditText setLoginField;
        private final Button next;
        private final TextView haveAnAccountLink;

        public Views() {
            setLoginField = findViewById(R.id.info_for_name);
            next = findViewById(R.id.let_name_next);
            haveAnAccountLink = findViewById(R.id.link_log_in);
        }
    }

    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        views = new Views();

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private void setIntents() {
        if (Intents.getSetPassword() == null)
            Intents.setSetPassword(new Intent(this, SetPassword.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
    }

    private void setListeners() {
        views.next.setOnClickListener(v -> {
            if (views.setLoginField.length() != 0) {
                String login = views.setLoginField.getText().toString().trim();
                Cache.saveSP(this, CacheScopes.USER_LOGIN.toString(), login);
                RegistrationActivities.activityList.add(this);
                startActivity(Intents.getSetPassword());
            } else {
                Resources.getToast(this, getResources().getString(R.string.error_send_password1)).show();
            }
        });

        views.haveAnAccountLink.setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
            RegistrationActivities.deleteActivities();
        });
    }
}