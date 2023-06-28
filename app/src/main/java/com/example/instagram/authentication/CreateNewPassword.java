package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.example.instagram.services.Animation;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Errors;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateNewPassword extends AppCompatActivity {
    private class Views {
        private final LinearLayout createNewPasswordLayout;
        private final Spinner languagesSpinner;
        private final TextView createNewPasswordTitle;
        private final TextView createNewPasswordDescription;
        private final EditText fieldPassword;
        private final EditText fieldRepeatPassword;
        private final Button showPassword;
        private final Button showRepeatPassword;
        private final Button next;
        private final ImageView warning;
        private final TextView haveAnAccount;
        private final TextView haveAnAccountLink;

        public Views() {
            createNewPasswordLayout = findViewById(R.id.create_new_password);
            languagesSpinner = findViewById(R.id.languages);
            createNewPasswordTitle = findViewById(R.id.create_new_password_title);
            createNewPasswordDescription = findViewById(R.id.create_new_password_info);
            fieldPassword = findViewById(R.id.info_for_password);
            fieldRepeatPassword = findViewById(R.id.info_for_password_repeat);
            showPassword = findViewById(R.id.auth_eye);
            showRepeatPassword = findViewById(R.id.auth_eye_repeat);
            next = findViewById(R.id.next);
            haveAnAccount = findViewById(R.id.reg_question);
            haveAnAccountLink = findViewById(R.id.link_log_in);
            warning = findViewById(R.id.validation_error);
        }
    }

    private Resources resources;
    private Localisation localisation;
    private Views views;
    private boolean passwordEyeState = false;
    private boolean passwordEyeStateRepeat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);

        resources = getResources();
        views = new Views();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
        Animation.getAnimations(views.createNewPasswordLayout).start();
    }

    private void setIntents() {
        if (Intents.getAuthorisation() == null) {
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
        }
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
                views.fieldPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                views.showPassword.setText(resources.getString(R.string.hide_password));
            } else {
                views.fieldPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                views.showPassword.setText(resources.getString(R.string.show_password));
            }
        });

        views.showRepeatPassword.setOnClickListener(v -> {
            passwordEyeStateRepeat = !passwordEyeStateRepeat;

            if (passwordEyeStateRepeat) {
                views.fieldRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                views.showRepeatPassword.setText(resources.getString(R.string.hide_password));
            } else {
                views.fieldRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                views.showRepeatPassword.setText(resources.getString(R.string.show_password));
            }
        });

        views.fieldPassword.addTextChangedListener(new Validator(views.fieldPassword) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editText.setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validatePassword(text, resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, CreateNewPassword.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, CreateNewPassword.this.getTheme()));
                }

                if (views.fieldRepeatPassword.getText().toString().equals(editText.getText().toString())) {
                    views.fieldRepeatPassword.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, CreateNewPassword.this.getTheme()));
                } else {
                    views.fieldRepeatPassword.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, CreateNewPassword.this.getTheme()));
                }
            }
        });

        views.fieldRepeatPassword.addTextChangedListener(new Validator(views.fieldRepeatPassword) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editText.setTextColor(resources.getColor(R.color.white, getTheme()));

                if (editText.getText().toString().equals(views.fieldPassword.getText().toString())) {
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, CreateNewPassword.this.getTheme()));
                } else {
                    setValidationError(true, resources.getString(R.string.password_repeat_error));
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, CreateNewPassword.this.getTheme()));
                }
            }
        });

        views.next.setOnClickListener(v -> {
            if (views.warning.getVisibility() == View.GONE) {
                try {
                    // region Check to next
                    Validations.validatePassword(views.fieldPassword.getText().toString(), resources);

                    if (!views.fieldRepeatPassword.getText().toString().equals(views.fieldPassword.getText().toString())) {
                        throw new Exception(resources.getString(R.string.password_repeat_error));
                    }
                    // endregion

                    setValidationError(false, "");
                    TransitUser.user.setPassword(views.fieldPassword.getText().toString().trim());
                    TransitUser.user.setPasswordRepeat(views.fieldRepeatPassword.getText().toString().trim());

                    Services.sendNewPasswordAfterForgot(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            assert response.body() != null;
                            Errors.afterRestorePassword(getApplicationContext(), response.body()).show();

                            if (response.body().contains("0")) {
                                startActivity(Intents.getAuthorisation());
                                finish();
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

    private void setStringResources() {
        views.next.setText(resources.getString(R.string.next_step));
        views.showPassword.setText(resources.getString(R.string.show_password));
        views.showRepeatPassword.setText(resources.getString(R.string.show_password));
        views.createNewPasswordTitle.setText(resources.getString(R.string.create_new_password_after_forgot));
        views.createNewPasswordDescription.setText(resources.getString(R.string.create_new_password_after_forgot_info));
        views.haveAnAccount.setText(resources.getString(R.string.have_an_acc));
        views.haveAnAccountLink.setText(resources.getString(R.string.log_in));
        views.fieldPassword.setHint(resources.getString(R.string.password_hint));
        views.fieldRepeatPassword.setHint(resources.getString(R.string.password_hint));
    }
}