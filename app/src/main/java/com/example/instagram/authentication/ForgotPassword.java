package com.example.instagram.authentication;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.UiVisibility;

import org.json.JSONException;

public class ForgotPassword extends AppCompatActivity {
    private class Views {
        private final TextView forgotPasswordTitle;
        private final TextView forgotPasswordDescription;
        private final EditText fieldForCode;
        private final Button confirmCode;
        private final Spinner languagesSpinner;

        public Views() {
            languagesSpinner = findViewById(R.id.languages);
            confirmCode = findViewById(R.id.confirm_code_button);
            fieldForCode = findViewById(R.id.code);
            forgotPasswordDescription = findViewById(R.id.forgot_info);
            forgotPasswordTitle = findViewById(R.id.question);
        }
    }

    private Resources resources;
    private Localisation localisation;
    private Handler handler;
    private Runnable runnable;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        views = new Views();
        resources = getResources();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());
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
        };
    }

    private void setIntents() {
        Intents.setRegistration(new Intent(this, Registration.class));
        Intents.setCreateNewPassword(new Intent(this, CreateNewPassword.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
    }

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

    private void setStringResources() {
        views.confirmCode.setText(resources.getString(R.string.confirm_code));
        views.forgotPasswordTitle.setText(resources.getString(R.string.can_not_login));
        views.forgotPasswordDescription.setText(resources.getString(R.string.confirm_code_text));
        views.fieldForCode.setHint(resources.getString(R.string.mail_code));
    }
}