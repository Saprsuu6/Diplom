package com.example.instagram.authentication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.UiVisibility;

import org.json.JSONException;

public class ForgotPassword extends AppCompatActivity {
    private class Views {

        private final EditText fieldForCode;
        private final Button confirmCode;

        public Views() {
            confirmCode = findViewById(R.id.confirm_code_button);
            fieldForCode = findViewById(R.id.code);
        }
    }

    private Handler handler;
    private Runnable runnable;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        views = new Views();
        handler = new Handler();
        runnable = checkUsedLink();
        handler.postDelayed(runnable, 5000L);

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private Runnable checkUsedLink() {
        return () -> {
            String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());

            try {
                new DoCallBack().setValues(() -> {
                    startActivity(Intents.getCreateNewPassword());
                    handler.removeCallbacks(runnable);
                    finish();
                }, this, new Object[]{login, handler, runnable}).sendToCheckUsedLickInMailPassword();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            handler.postDelayed(runnable, 5000L);
        };
    }

    private void setIntents() {
        Intents.setRegistration(new Intent(this, Registration.class));
        Intents.setCreateNewPassword(new Intent(this, CreateNewPassword.class));
    }

    private void setListeners() {
        views.confirmCode.setOnClickListener(v -> {
            if (views.fieldForCode.length() != 0) {
                String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
                String code = views.fieldForCode.getText().toString().trim();

                try {
                    new DoCallBack().setValues(() -> {
                        startActivity(Intents.getCreateNewPassword());
                        finish();
                    }, this, new Object[]{login, code, (Runnable) () -> {
                        startActivity(Intents.getAuthorisation());
                        finish();
                    }}).sendToCheckUserCode();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}