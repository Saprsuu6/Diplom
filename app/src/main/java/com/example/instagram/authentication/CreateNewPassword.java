package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import org.json.JSONObject;

public class CreateNewPassword extends AppCompatActivity {
    private class Views {
        private final EditText fieldPassword;
        private final EditText fieldRepeatPassword;
        private final ImageView showPassword;
        private final ImageView showRepeatPassword;
        private final Button next;
        private final ImageView warning;
        private final TextView haveAnAccountLink;

        public Views() {
            fieldPassword = findViewById(R.id.info_for_password);
            fieldRepeatPassword = findViewById(R.id.info_for_password_repeat);
            showPassword = findViewById(R.id.auth_eye);
            showRepeatPassword = findViewById(R.id.auth_eye_repeat);
            next = findViewById(R.id.next);
            haveAnAccountLink = findViewById(R.id.link_log_in);
            warning = findViewById(R.id.validation_error);
        }
    }
    
    private Views views;
    private boolean passwordEyeState = false;
    private boolean passwordEyeStateRepeat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);
        
        views = new Views();

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private void setIntents() {
        if (Intents.getAuthorisation() == null) {
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setListeners() {
        views.showPassword.setOnClickListener(v -> {
            passwordEyeState = !passwordEyeState;

            if (passwordEyeState) {
                views.fieldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                views.showPassword.setImageDrawable(getResources().getDrawable(R.drawable.hide, getTheme()));
            } else {
                views.fieldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                views.showPassword.setImageDrawable(getResources().getDrawable(R.drawable.show, getTheme()));
            }
        });

        views.showRepeatPassword.setOnClickListener(v -> {
            passwordEyeStateRepeat = !passwordEyeStateRepeat;

            if (passwordEyeStateRepeat) {
                views.fieldRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                views.showRepeatPassword.setImageDrawable(getResources().getDrawable(R.drawable.hide, getTheme()));
            } else {
                views.fieldRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                views.showRepeatPassword.setImageDrawable(getResources().getDrawable(R.drawable.show, getTheme()));
            }
        });

        views.fieldPassword.addTextChangedListener(new Validator(views.fieldPassword) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editText.setTextColor(getResources().getColor(R.color.white, getTheme()));

                try {
                    Validations.validatePassword(text, getResources());
                    setValidationError(false, "");
                    editText.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, CreateNewPassword.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, CreateNewPassword.this.getTheme()));
                }

                if (views.fieldRepeatPassword.getText().toString().equals(editText.getText().toString())) {
                    setValidationError(false, "");
                    views.fieldRepeatPassword.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, CreateNewPassword.this.getTheme()));
                } else {
                    setValidationError(true, getResources().getString(R.string.password_repeat_error));
                    views.fieldRepeatPassword.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, CreateNewPassword.this.getTheme()));
                }
            }
        });

        views.fieldRepeatPassword.addTextChangedListener(new Validator(views.fieldRepeatPassword) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editText.setTextColor(getResources().getColor(R.color.white, getTheme()));

                if (editText.getText().toString().equals(views.fieldPassword.getText().toString())) {
                    setValidationError(false, "");
                    editText.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, CreateNewPassword.this.getTheme()));
                } else {
                    setValidationError(true, getResources().getString(R.string.password_repeat_error));
                    editText.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, CreateNewPassword.this.getTheme()));
                }
            }
        });

        views.next.setOnClickListener(v -> {
            if (views.warning.getVisibility() == View.GONE) {
                try {
                    // region Check to next
                    Validations.validatePassword(views.fieldPassword.getText().toString(), getResources());

                    if (!views.fieldRepeatPassword.getText().toString().equals(views.fieldPassword.getText().toString())) {
                        throw new Exception(getResources().getString(R.string.password_repeat_error));
                    }
                    // endregion

                    setValidationError(false, "");

                    String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
                    String code = Cache.loadStringSP(this, CacheScopes.USER_EMAIL_CODE.toString());
                    String password = views.fieldPassword.getText().toString().trim();
                    String rpPassword = views.fieldRepeatPassword.getText().toString().trim();
                    JSONObject jsonObject = User.getJSONAfterForgotPassword(login, code, password, rpPassword);

                    new DoCallBack().setValues(() -> {
                        startActivity(Intents.getAuthorisation());
                        finish();
                    }, this, new Object[]{jsonObject}).sendNewPasswordAfterForgot();
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
            }
        });

        views.haveAnAccountLink.setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
        });
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
}