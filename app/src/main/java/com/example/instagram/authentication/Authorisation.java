package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.authentication.after_reg.SetPassword;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.MyRetrofit;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.Resources;
import com.example.instagram.services.Services;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class Authorisation extends AppCompatActivity {
    private class Views {
        public final EditText fieldForLogin;
        public final EditText fieldForPassword;
        public final TextView gmailName;
        public final TextView linkForgotPassword;
        public final TextView notHaveAnAccount;
        public final TextView singInLink;
        public final CheckBox rememberMe;
        public final ImageView warning;
        public final Button logIn;
        public final ImageView showPassword;

        public Views() {
            fieldForLogin = findViewById(R.id.auth_login);
            fieldForPassword = findViewById(R.id.auth_pass);
            gmailName = findViewById(R.id.email_name);
            linkForgotPassword = findViewById(R.id.auth_forgot_pass);
            notHaveAnAccount = findViewById(R.id.reg_question);
            singInLink = findViewById(R.id.link_log_in);
            rememberMe = findViewById(R.id.remember_flag);
            logIn = findViewById(R.id.auth_log_in);
            showPassword = findViewById(R.id.auth_eye);
            warning = findViewById(R.id.validation_error);
        }
    }
    
    private Views views;
    private boolean passwordEyeState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorisation);

        //region initialise link
        Services.BASE_URL = getString(R.string.link_tomcat);
        Services.retrofit = MyRetrofit.initializeRetrofit(Services.BASE_URL);
        //endregion
        views = new Views();

        // TODO delete someday
        views.fieldForLogin.setText("Andry");
        views.fieldForPassword.setText("MyNewPass123!");

        setIntents();

        try {
            if (setRememberMe()) return;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        setListeners();
        UiVisibility.setUiVisibility(this);
        Permissions.setPermissions(Authorisation.this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permissions.STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("storage granted");
                } else if (grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("read contacts granted");
                } else if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("camera granted");
                }
            }
        }
    }

    private boolean setRememberMe() throws JSONException {
        boolean rememberMeFlag = Cache.loadBoolSP(this, CacheScopes.REMEMBER_ME.toString());

        String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
        String password = Cache.loadStringSP(this, CacheScopes.USER_PASSWORD.toString());
        JSONObject jsonObject = User.getJSONToCheck(login, password);

        if (rememberMeFlag) {
            new DoCallBack().setValues(() -> {
                Cache.saveSP(this, CacheScopes.USER_LOGIN.toString(), login);
                Cache.saveSP(this, CacheScopes.USER_PASSWORD.toString(), password);

                startActivity(Intents.getNewsList());
                finish();
            }, this, new Object[]{jsonObject}).sendToLogIn();
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
        DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setListeners() {
        views.showPassword.setOnClickListener(v -> {
            passwordEyeState = !passwordEyeState;

            if (passwordEyeState) {
                views.fieldForPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                Resources.setDrawableIntoImageView(getResources().getDrawable(R.drawable.hide, getTheme()),  views.showPassword);
            } else {
                views.fieldForPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                Resources.setDrawableIntoImageView(getResources().getDrawable(R.drawable.show, getTheme()),  views.showPassword);
            }
        });

        views.fieldForLogin.addTextChangedListener(new Validator(views.fieldForLogin) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                Resources.setTextColor(getResources().getColor(R.color.white, getTheme()), editText);

                try {
                    Validations.validateUserName(text, getResources());
                    setValidationError(false, "");
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()), editText);
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()), editText);
                }
            }
        });

        views.fieldForPassword.addTextChangedListener(new Validator(views.fieldForPassword) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                Resources.setTextColor(getResources().getColor(R.color.white, getTheme()), editText);

                try {
                    Validations.validatePassword(text, getResources());
                    setValidationError(false, "");
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()), editText);
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()), editText);
                }
            }
        });

        // authorisation
        views.logIn.setOnClickListener(v -> {
            if (views.fieldForLogin.length() != 0 && views.fieldForPassword.length() != 0) {
                try {
                    Validations.validateUserName(views.fieldForLogin.getText().toString(), getResources());
                    Validations.validatePassword(views.fieldForPassword.getText().toString(), getResources());
                    setValidationError(false, "");

                    if (views.rememberMe.isChecked()) {
                        Cache.saveSP(this, CacheScopes.REMEMBER_ME.toString(), views.rememberMe.isChecked());
                    }

                    Cache.saveSP(this, CacheScopes.USER_LOGIN.toString(), views.fieldForLogin.getText().toString());
                    JSONObject jsonObject = User.getJSONToCheck(views.fieldForLogin.getText().toString(), views.fieldForPassword.getText().toString());

                    new DoCallBack().setValues(() -> {
                        Cache.saveSP(this, CacheScopes.USER_PASSWORD.toString(), views.fieldForPassword.getText().toString());
                        startActivity(Intents.getNewsList());
                        finish();
                    }, this, new Object[]{jsonObject}).sendToLogIn();
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                Resources.getToast(this, getResources().getString(R.string.error_send_password1)).show();
            }
        });

        // forgot password
        views.linkForgotPassword.setOnClickListener(v -> {
            if (!Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString()).equals("")) {
                try {
                    JSONObject jsonObject = User.getJSONLogin(views.fieldForLogin.getText().toString().trim());
                    // send code to restore password
                    new DoCallBack().setValues(null, this, new Object[]{jsonObject}).sendToCodeToRestorePassword();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

                startActivity(Intents.getForgotPassword());
            } else {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.auth_attempt), Toast.LENGTH_SHORT).show();
            }
        });

        views.singInLink.setOnClickListener(v -> startActivity(Intents.getRegistration()));
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