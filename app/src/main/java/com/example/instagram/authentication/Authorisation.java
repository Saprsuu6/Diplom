package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.R;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.Animation;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Errors;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import org.json.JSONException;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Authorisation extends AppCompatActivity {
    private class Views {
        public final LinearLayout authorisationLayout;
        public final EditText fieldForLogin;
        public final EditText fieldForPassword;
        public final TextView gmailName;
        public final TextView linkForgotPassword;
        public final TextView dontHaveAnAccount;
        public final TextView singInLink;
        public final CheckBox rememberMe;
        public final ImageView warning;
        public final Button logIn;
        public final Button showPassword;
        public final Spinner languagesSpinner;

        public Views() {
            authorisationLayout = findViewById(R.id.authorisation);
            fieldForLogin = findViewById(R.id.auth_login);
            fieldForPassword = findViewById(R.id.auth_pass);
            gmailName = findViewById(R.id.email_name);
            linkForgotPassword = findViewById(R.id.auth_forgot_pass);
            dontHaveAnAccount = findViewById(R.id.reg_question);
            singInLink = findViewById(R.id.link_log_in);
            rememberMe = findViewById(R.id.remember_flag);
            logIn = findViewById(R.id.auth_log_in);
            languagesSpinner = findViewById(R.id.languages);
            showPassword = findViewById(R.id.auth_eye);
            warning = findViewById(R.id.validation_error);
        }
    }

    private Resources resources;
    private Localisation localisation;
    private Views views;
    private boolean passwordEyeState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorisation);

        views = new Views();
        resources = getResources();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());
        // TODO delete someday
        views.fieldForLogin.setText("Andry");
        views.fieldForPassword.setText("MyNewPass2929!");

        try {
            if (setRememberMe()) return;
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
        Animation.getAnimations(views.authorisationLayout).start();
        if (!TransitUser.user.getOtherInfo().isPhoneBookPermission() || !TransitUser.user.getOtherInfo().isMediaPermission())
            setPermissions();
    }

    private boolean setRememberMe() throws JSONException {
        boolean rememberMeFlag = com.example.instagram.services.SharedPreferences.loadBoolSP(this, "rememberMe");

        if (rememberMeFlag) {
            TransitUser.user.setLogin(com.example.instagram.services.SharedPreferences.loadStringSP(this, "login"));
            TransitUser.user.setPhoneNumber(com.example.instagram.services.SharedPreferences.loadStringSP(this, "phone"));
            TransitUser.user.setEmail(com.example.instagram.services.SharedPreferences.loadStringSP(this, "email"));
            TransitUser.user.setPassword(com.example.instagram.services.SharedPreferences.loadStringSP(this, "password"));

            Services.authorizeUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseStr = response.body();

                        // region get token from response
                        int indexFrom = responseStr.indexOf(":");
                        String token = responseStr.substring(indexFrom + 1).trim();
                        // endregion

                        TransitUser.user.setToken(token);
                        if (response.body().contains("0")) {
                            startActivity(Intents.getNewsList());
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "User are not authorized", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            });

            startActivity(Intents.getNewsList());
            finish();
            return true;
        }

        return false;
    }


    private void setIntents() {
        Intents.setRegistration(new Intent(this, Registration.class));
        Intents.setForgotPassword(new Intent(this, ForgotPassword.class));
        Intents.setNewsList(new Intent(this, NewsLine.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
    }

    private void setPermissions() {
        AlertDialog.Builder permissionsDialog = Permissions.getPermissionDialog(this, resources);
        permissionsDialog.setNegativeButton("Cancel", (dialog1, which) -> finish());
        permissionsDialog.create().show();
    }

    private void setListeners() {
        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();

                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

        views.showPassword.setOnClickListener(v -> {
            passwordEyeState = !passwordEyeState;

            if (passwordEyeState) {
                views.fieldForPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                views.showPassword.setText(resources.getString(R.string.hide_password));
            } else {
                views.fieldForPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                views.showPassword.setText(resources.getString(R.string.show_password));
            }
        });

        views.fieldForLogin.addTextChangedListener(new Validator(views.fieldForLogin) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editText.setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validateUserName(text, resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, Authorisation.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, Authorisation.this.getTheme()));
                }
            }
        });

        views.fieldForPassword.addTextChangedListener(new Validator(views.fieldForPassword) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editText.setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validatePassword(text, resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, Authorisation.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, Authorisation.this.getTheme()));
                }
            }
        });

        // authorisation
        views.logIn.setOnClickListener(v -> {
            if (views.fieldForLogin.length() != 0 && views.fieldForPassword.length() != 0) {
                try {
                    Validations.validateUserName(views.fieldForLogin.getText().toString(), resources);
                    Validations.validatePassword(views.fieldForPassword.getText().toString(), resources);
                    setValidationError(false, "");

                    TransitUser.user.setLogin(views.fieldForLogin.getText().toString());
                    TransitUser.user.setPassword(views.fieldForPassword.getText().toString());

                    if (views.rememberMe.isChecked()) {
                        com.example.instagram.services.SharedPreferences.saveSP(this, "rememberMe", views.rememberMe.isChecked());
                        com.example.instagram.services.SharedPreferences.saveSP(this, "login", TransitUser.user.getLogin());
                        com.example.instagram.services.SharedPreferences.saveSP(this, "phone", TransitUser.user.getPhoneNumber());
                        com.example.instagram.services.SharedPreferences.saveSP(this, "email", TransitUser.user.getEmail());
                        com.example.instagram.services.SharedPreferences.saveSP(this, "password", TransitUser.user.getPassword());
                    }
                    Services.authorizeUser(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String responseStr = response.body();

                                if (responseStr.contains("0")) {
                                    // region get token from response
                                    int indexFrom = responseStr.indexOf(":");
                                    String token = responseStr.substring(indexFrom + 1).trim();
                                    // endregion

                                    TransitUser.user.setToken(token);

                                    startActivity(Intents.getNewsList());
                                    finish();
                                } else {
                                    Toast.makeText(getApplicationContext(), "User are not authorized", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                Toast.makeText(this, resources.getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
            }
        });

        // forgot password
        views.linkForgotPassword.setOnClickListener(v -> {
            if (TransitUser.user.getLogin() != null) {
                try {
                    Services.sendToForgotPassword(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Errors.forgotPasswordCode(getApplicationContext(), response.body()).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                startActivity(Intents.getForgotPassword());
            } else {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.auth_attempt), Toast.LENGTH_SHORT).show();
            }
        });

        views.singInLink.setOnClickListener(v -> startActivity(Intents.getRegistration()));
    }

    private void setValidationError(boolean temp, String message) {
        if (temp) {
            views.warning.setVisibility(View.VISIBLE);
            Animation.getAnimations(views.warning).start();
        } else {
            views.warning.setVisibility(View.GONE);
            Animation.getAnimations(views.warning).stop();
        }

        TooltipCompat.setTooltipText(views.warning, message);
    }

    private void setStringResources() {
        views.fieldForLogin.setHint(resources.getString(R.string.login_hint_username));
        views.fieldForPassword.setHint(resources.getString(R.string.password_hint));
        views.showPassword.setText(resources.getString(R.string.show_password));
        views.logIn.setText(resources.getString(R.string.log_in));
        views.linkForgotPassword.setText(resources.getString(R.string.password_forgot));
        views.dontHaveAnAccount.setText(resources.getString(R.string.do_not_have_an_acc));
        views.singInLink.setText(resources.getString(R.string.sing_in));
        views.gmailName.setText(resources.getString(R.string.gmail));
    }
}