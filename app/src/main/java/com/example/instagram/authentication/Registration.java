package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.R;
import com.example.instagram.authentication.after_reg.SetName;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.GetEthernetInfo;
import com.example.instagram.services.Intents;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.Resources;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

public class Registration extends AppCompatActivity {
    private class Views {
        private final EditText fieldToEmail;
        private final Button singIn;
        private final ImageView warning;
        private final CheckBox receiveEmail;
        private final CheckBox hideEmail;
        private final CheckBox rememberMe;
        private final TextView emailName;
        private final TextView haveAnAccountLink;

        public Views() {
            fieldToEmail = findViewById(R.id.field_email);
            singIn = findViewById(R.id.reg_log_in);
            receiveEmail = findViewById(R.id.receive_news_letters);
            hideEmail = findViewById(R.id.hide_email);
            rememberMe = findViewById(R.id.remember_flag);
            haveAnAccountLink = findViewById(R.id.link_log_in);
            warning = findViewById(R.id.validation_error);
            emailName = findViewById(R.id.email_name);
        }
    }
    
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgistration);

        views = new Views();

        setRememberMe();
        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private void setIntents() {
        if (Intents.getSetName() == null) {
            Intents.setSetName(new Intent(this, SetName.class));
        }
    }

    private void setRememberMe() {
        boolean rememberMeFlag = Cache.loadBoolSP(this, "rememberMe");
        views.rememberMe.setChecked(rememberMeFlag);
    }

    private void setListeners() {
        views.rememberMe.setOnCheckedChangeListener((buttonView, isChecked) -> Cache.saveSP(this, "rememberMe", isChecked));

        views.fieldToEmail.addTextChangedListener(new Validator(views.fieldToEmail) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                Resources.setTextColor(getResources().getColor(R.color.white, getTheme()), editText);

                try {
                    Validations.validateEmail(text, views.emailName.getText().toString(), getResources());
                    setValidationError(false, "");
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()), editText);
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()), editText);
                }
            }
        });

        views.haveAnAccountLink.setOnClickListener(v -> finish());

        // send code to email
        views.singIn.setOnClickListener(v -> {
            if (views.warning.getVisibility() == View.GONE) {
                setValidationError(false, "");

                // save email
                Cache.saveSP(this, CacheScopes.USER_EMAIL.toString(), views.fieldToEmail.getText().toString().trim() + views.emailName.getText());

                Cache.saveSP(this, CacheScopes.USER_IS_RECEIVE_LETTERS.toString(), views.receiveEmail.isChecked());
                Cache.saveSP(this, CacheScopes.USER_IS_HIDE_EMAIL.toString(), views.hideEmail.isChecked());

                String ip = GetEthernetInfo.getNetworkInfo();
                if (ip != null) Cache.saveSP(this, CacheScopes.USER_IP.toString(), ip);

                if (!RegistrationActivities.activityList.contains(this)) {
                    RegistrationActivities.activityList.add(this);
                }

                startActivity(Intents.getSetName());
            }
        });
    }

    private void setValidationError(boolean temp, String message) {
        if (temp) {
            Resources.setVisibility(View.VISIBLE, views.warning);
            Animation.getAnimations(views.warning).start();
        } else {
            Resources.setVisibility(View.GONE, views.warning);
            Animation.getAnimations(views.warning).stop();
        }

        TooltipCompat.setTooltipText(views.warning, message);
    }
}