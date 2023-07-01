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
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.Errors;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Services;
import com.example.instagram.services.UiVisibility;

import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
                Services.sendToCheckUsedLickInMail(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().contains("0")) {
                                int index = response.body().indexOf(":");
                                String code = response.body().substring(index + 1);

                                // save user repair password email
                                Cache.saveSP(getApplicationContext(), CacheScopes.USER_EMAIL_CODE.toString(), code.trim());

                                startActivity(Intents.getCreateNewPassword());
                                handler.removeCallbacks(runnable);
                                finish();
                            } else {
                                handler.postDelayed(runnable, 5000L);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        System.out.println(t.getMessage());
                    }
                }, login);

            } catch (JSONException e) {
                System.out.println(e.getMessage());
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
                    Services.sendToCheckUserCode(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                            String strResponse = response.body();
                            assert strResponse != null;
                            assert response.body() != null;

                            Errors.enterCode(getApplicationContext(), response.body()).show();

                            if (strResponse.contains("1")) {
                                char symbol = strResponse.charAt(strResponse.length() - 1);
                                int attempts = Integer.parseInt(String.valueOf(symbol));

                                if (attempts > 0) {
                                    Toast.makeText(getApplicationContext(), attempts, Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(Intents.getAuthorisation());
                                    finish();
                                }
                            }

                            int index = response.body().indexOf(":");
                            String code = response.body().substring(index + 1);

                            // save user repair password email
                            Cache.saveSP(getApplicationContext(), CacheScopes.USER_EMAIL_CODE.toString(), code.trim());

                            startActivity(Intents.getCreateNewPassword());
                            finish();
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            System.out.println("Error: " + t.getMessage());
                        }
                    }, login, code);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
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