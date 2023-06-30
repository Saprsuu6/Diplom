package com.example.instagram.authentication.after_reg;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

public class SetPassword extends AppCompatActivity {
    private class Views {
        private final LinearLayout addPasswordLayout;
        private final Spinner languagesSpinner;
        private final TextView addPasswordTitle;
        private final EditText fieldPassword;
        private final EditText fieldRepeatPassword;
        private final Button showPassword;
        private final ImageView warning;
        private final Button showRepeatPassword;
        private final Button next;
        private final TextView haveAnAccount;
        private final TextView haveAnAccountLink;

        public Views() {
            addPasswordLayout = findViewById(R.id.set_password);
            languagesSpinner = findViewById(R.id.languages);
            addPasswordTitle = findViewById(R.id.let_info);
            fieldPassword = findViewById(R.id.info_for_password);
            fieldRepeatPassword = findViewById(R.id.info_for_password_repeat);
            showPassword = findViewById(R.id.auth_eye);
            showRepeatPassword = findViewById(R.id.auth_eye_repeat);
            next = findViewById(R.id.let_name_next);
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
        setContentView(R.layout.activity_set_password);
        setUiVisibility();

        resources = getResources();
        setIntents();

        views = new Views();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private void setIntents() {
        if (Intents.getSetBirthday() == null)
            Intents.setSetBirthday(new Intent(this, SetBirthday.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
                views.showRepeatPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                views.showRepeatPassword.setText(resources.getString(R.string.hide_password));
            } else {
                views.showRepeatPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
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
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
                }

                if (views.fieldRepeatPassword.getText().toString().equals(editText.getText().toString())) {
                    views.fieldRepeatPassword.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } else {
                    views.fieldRepeatPassword.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
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
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } else {
                    setValidationError(true, resources.getString(R.string.password_repeat_error));
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
                }
            }
        });


        views.next.setOnClickListener(v -> {
            if (views.fieldPassword.length() != 0) {
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

                    RegistrationActivities.activityList.add(this);
                    startActivity(Intents.getSetBirthday());
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

    private void setStringResources() {
        views.next.setText(resources.getString(R.string.next_step));
        views.showRepeatPassword.setText(resources.getString(R.string.show_password));
        views.showPassword.setText(resources.getString(R.string.show_password));
        views.addPasswordTitle.setText(resources.getString(R.string.let_password));
        views.haveAnAccount.setText(resources.getString(R.string.have_an_acc));
        views.haveAnAccountLink.setText(resources.getString(R.string.log_in));
        views.fieldPassword.setHint(resources.getString(R.string.password_hint));
        views.fieldRepeatPassword.setHint(resources.getString(R.string.password_hint_repeat));
    }
}