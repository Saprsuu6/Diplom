package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.Intents;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

public class SetPassword extends AppCompatActivity {
    private class Views {
        private final EditText fieldPassword;
        private final EditText fieldRepeatPassword;
        private final Button showPassword;
        private final Button showRepeatPassword;
        private final ImageView warning;
        private final Button next;
        private final TextView haveAnAccountLink;

        public Views() {
            fieldPassword = findViewById(R.id.info_for_password);
            fieldRepeatPassword = findViewById(R.id.info_for_password_repeat);
            showPassword = findViewById(R.id.auth_eye);
            showRepeatPassword = findViewById(R.id.auth_eye_repeat);
            next = findViewById(R.id.let_name_next);
            haveAnAccountLink = findViewById(R.id.link_log_in);
            warning = findViewById(R.id.validation_error);
        }
    }

    private Views views;
    private boolean passwordEyeState = false;
    private boolean passwordEyeStateRepeat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        setUiVisibility();

        setIntents();

        views = new Views();

        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private void setIntents() {
        if (Intents.getSetBirthday() == null)
            Intents.setSetBirthday(new Intent(this, SetBirthday.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setListeners() {
        views.showPassword.setOnClickListener(v -> {
            passwordEyeState = !passwordEyeState;

            if (passwordEyeState) {
                views.fieldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                views.showPassword.setText(getResources().getString(R.string.hide_password));
            } else {
                views.fieldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                views.showPassword.setText(getResources().getString(R.string.show_password));
            }
        });

        views.showRepeatPassword.setOnClickListener(v -> {
            passwordEyeStateRepeat = !passwordEyeStateRepeat;

            if (passwordEyeStateRepeat) {
                views.fieldRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                views.showRepeatPassword.setText(getResources().getString(R.string.hide_password));
            } else {
                views.fieldRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                views.showRepeatPassword.setText(getResources().getString(R.string.show_password));
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
                    editText.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
                }

                if (views.fieldRepeatPassword.getText().toString().equals(editText.getText().toString())) {
                    setValidationError(false, "");
                    views.fieldRepeatPassword.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } else {
                    setValidationError(true, getResources().getString(R.string.password_repeat_error));
                    views.fieldRepeatPassword.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
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
                    editText.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } else {
                    setValidationError(true, getResources().getString(R.string.password_repeat_error));
                    editText.setBackground(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
                }
            }
        });


        views.next.setOnClickListener(v -> {
            if (views.fieldPassword.length() != 0) {
                try {
                    // region Check to next
                    Validations.validatePassword(views.fieldPassword.getText().toString(), getResources());

                    if (!views.fieldRepeatPassword.getText().toString().equals(views.fieldPassword.getText().toString())) {
                        throw new Exception(getResources().getString(R.string.password_repeat_error));
                    }
                    // endregion

                    setValidationError(false, "");

                    // set passwords
                    String password = views.fieldPassword.getText().toString().trim();
                    String rpPassword = views.fieldRepeatPassword.getText().toString().trim();
                    Cache.saveSP(this, CacheScopes.USER_PASSWORD.toString(), password);
                    Cache.saveSP(this, CacheScopes.USER_PASSWORD_REPEAT.toString(), rpPassword);

                    RegistrationActivities.activityList.add(this);
                    startActivity(Intents.getSetBirthday());
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
            RegistrationActivities.deleteActivities();
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