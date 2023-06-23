package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
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
import com.example.instagram.authentication.CreateNewPassword;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.Validator;
import com.example.instagram.services.Validations;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;

public class SetPassword extends AppCompatActivity {
    private Resources resources;
    private LinearLayout setPassword;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private EditText[] editTexts;
    private Button[] buttons;
    private ImageView[] imageViews;
    private boolean passwordEyeState = false;
    private boolean passwordEyeStateRepeat = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_password);
        setUiVisibility();

        resources = getResources();
        setIntents();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        setAnimations(setPassword).start();
        setListeners();
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
        Localisation.setFirstLocale(languages);
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void findViews() {
        setPassword = findViewById(R.id.set_password);
        languages = findViewById(R.id.languages);
        editTexts = new EditText[]{findViewById(R.id.info_for_password), findViewById(R.id.info_for_password_repeat)};
        buttons = new Button[]{findViewById(R.id.let_name_next), findViewById(R.id.auth_eye), findViewById(R.id.auth_eye_repeat)};
        textViews = new TextView[]{findViewById(R.id.let_info),
                findViewById(R.id.reg_question),
                findViewById(R.id.link_log_in)};
        imageViews = new ImageView[]{findViewById(R.id.validation_error)};
    }

    private AnimationDrawable setAnimations(View view) {
        AnimationDrawable animationDrawable = (AnimationDrawable) view.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        return animationDrawable;
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
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        buttons[1].setOnClickListener(v -> {
            passwordEyeState = !passwordEyeState;

            if (passwordEyeState) {
                editTexts[0].setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                buttons[1].setText(resources.getString(R.string.hide_password));
            } else {
                editTexts[0].setTransformationMethod(PasswordTransformationMethod.getInstance());
                buttons[1].setText(resources.getString(R.string.show_password));
            }
        });

        buttons[2].setOnClickListener(v -> {
            passwordEyeStateRepeat = !passwordEyeStateRepeat;

            if (passwordEyeStateRepeat) {
                editTexts[1].setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                buttons[2].setText(resources.getString(R.string.hide_password));
            } else {
                editTexts[1].setTransformationMethod(PasswordTransformationMethod.getInstance());
                buttons[2].setText(resources.getString(R.string.show_password));
            }
        });

        editTexts[0].addTextChangedListener(new Validator(editTexts[0]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[0].setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validatePassword(text, resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
                }

                if (editTexts[1].getText().toString().equals(editTexts[0].getText().toString())) {
                    editTexts[1].setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } else {
                    editTexts[1].setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
                }
            }
        });

        editTexts[1].addTextChangedListener(new Validator(editTexts[1]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[0].setTextColor(resources.getColor(R.color.white, getTheme()));

                if (editTexts[1].getText().toString().equals(editTexts[0].getText().toString())) {
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, SetPassword.this.getTheme()));
                } else {
                    setValidationError(true, resources.getString(R.string.password_repeat_error));
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, SetPassword.this.getTheme()));
                }
            }
        });


        buttons[0].setOnClickListener(v -> {
            if (editTexts[0].length() != 0) {
                try {
                    // region Check to next
                    Validations.validatePassword(editTexts[0].getText().toString(), resources);

                    if (!editTexts[1].getText().toString().equals(editTexts[0].getText().toString())) {
                        throw new Exception(resources.getString(R.string.password_repeat_error));
                    }
                    // endregion

                    setValidationError(false, "");
                    TransitUser.user.setPassword(editTexts[0].getText().toString().trim());
                    TransitUser.user.setPasswordRepeat(editTexts[1].getText().toString().trim());

                    RegistrationActivities.activityList.add(this);
                    startActivity(Intents.getSetBirthday());
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                Toast.makeText(this, resources.getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
            }
        });

        textViews[2].setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
        });
    }

    private void setValidationError(boolean temp, String message) {
        if (temp) {
            imageViews[0].setVisibility(View.VISIBLE);
            setAnimations(imageViews[0]).start();
        } else {
            imageViews[0].setVisibility(View.GONE);
            setAnimations(imageViews[0]).stop();
        }

        TooltipCompat.setTooltipText(imageViews[0], message);
    }


    // region Localisation
    private void setStringResources() {
        buttons[0].setText(resources.getString(R.string.next_step));
        buttons[1].setText(resources.getString(R.string.show_password));
        buttons[2].setText(resources.getString(R.string.show_password));

        textViews[0].setText(resources.getString(R.string.let_password));
        textViews[1].setText(resources.getString(R.string.have_an_acc));
        textViews[2].setText(resources.getString(R.string.log_in));

        editTexts[0].setHint(resources.getString(R.string.password_hint));
        editTexts[1].setHint(resources.getString(R.string.password_hint_repeat));
    }

    // endregion
}