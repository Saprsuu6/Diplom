package com.example.instagram.authentication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import android.annotation.SuppressLint;
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

import com.example.instagram.R;
import com.example.instagram.authentication.after_reg.SetPassword;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

public class CreateNewPassword extends AppCompatActivity {
    private Resources resources;
    private LinearLayout createNewPassword;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private EditText[] editTexts;
    private Button[] buttons;
    private ImageView[] imageViews;
    private boolean passwordEyeState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_password);
        setUiVisibility();

        resources = getResources();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Localisation.setFirstLocale(languages);

        setAnimations(createNewPassword).start();
        setListeners();
    }

    @Override
    protected void onResume() {
        Localisation.setFirstLocale(languages);
        super.onResume();
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void findViews() {
        createNewPassword = findViewById(R.id.create_new_password);
        languages = findViewById(R.id.languages);
        editTexts = new EditText[]{findViewById(R.id.info_for_password)};
        buttons = new Button[]{findViewById(R.id.next), findViewById(R.id.auth_eye)};
        textViews = new TextView[]{findViewById(R.id.create_new_password_title),
                findViewById(R.id.create_new_password_info)};
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

        editTexts[0].addTextChangedListener(new Validator(editTexts[0]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[0].setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validatePassword(text, resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, CreateNewPassword.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, CreateNewPassword.this.getTheme()));
                }
            }
        });

        buttons[0].setOnClickListener(v -> {
            if (editTexts[0].length() != 0) {
                try {
                    Validations.validatePassword(editTexts[0].getText().toString(), resources);
                    setValidationError(false, "");
                    TransitUser.user.setPassword(editTexts[0].getText().toString().trim());

                    // TODO sent to the back end new password
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                Toast.makeText(this, resources.getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
            }
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

        textViews[0].setText(resources.getString(R.string.create_new_password_after_forgot));
        textViews[1].setText(resources.getString(R.string.create_new_password_after_forgot_info));

        editTexts[0].setHint(resources.getString(R.string.password_hint));
    }

    // endregion
}