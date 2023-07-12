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
import com.example.instagram.services.Resources;
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
                Resources.setDrawableIntoImageView(getResources().getDrawable(R.drawable.hide, getTheme()), views.showPassword);
            } else {
                views.fieldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                Resources.setDrawableIntoImageView(getResources().getDrawable(R.drawable.show, getTheme()), views.showPassword);
            }
        });

        views.showRepeatPassword.setOnClickListener(v -> {
            passwordEyeStateRepeat = !passwordEyeStateRepeat;

            if (passwordEyeStateRepeat) {
                views.fieldRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                Resources.setDrawableIntoImageView(getResources().getDrawable(R.drawable.hide, getTheme()), views.showRepeatPassword);
            } else {
                views.fieldRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                Resources.setDrawableIntoImageView(getResources().getDrawable(R.drawable.show, getTheme()), views.showRepeatPassword);
            }
        });

        views.fieldPassword.addTextChangedListener(new Validator(views.fieldPassword) {
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

                if (views.fieldRepeatPassword.getText().toString().equals(editText.getText().toString())) {
                    setValidationError(false, "");
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()), views.fieldRepeatPassword);
                } else {
                    setValidationError(true, getResources().getString(R.string.password_repeat_error));
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()), views.fieldRepeatPassword);
                }
            }
        });

        views.fieldRepeatPassword.addTextChangedListener(new Validator(views.fieldRepeatPassword) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                Resources.setTextColor(getResources().getColor(R.color.white, getTheme()), editText);

                if (editText.getText().toString().equals(views.fieldPassword.getText().toString())) {
                    setValidationError(false, "");
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()), editText);
                } else {
                    setValidationError(true, getResources().getString(R.string.password_repeat_error));
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()), editText);
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
                Resources.getToast(this, getResources().getString(R.string.error_send_password1)).show();
            }
        });

        views.haveAnAccountLink.setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
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