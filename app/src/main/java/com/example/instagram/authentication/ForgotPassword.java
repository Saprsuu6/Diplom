package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.instagram.services.Validations;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.Validator;

import java.util.Objects;

public class ForgotPassword extends AppCompatActivity {
    private Resources resources;
    private LinearLayout forgotPassword;
    private TextView[] textViews;
    private EditText[] editTexts;
    private Button[] buttons;
    private ImageView[] imageViews;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private Spinner infoTypes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        setUiVisibility();

        resources = getResources();
        setIntents();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Localisation.setFirstLocale(languages);

        setLoginTypes();

        setListeners();
        setAnimations(forgotPassword).start();
    }

    private void setIntents() {
        Intents.setRegistration(new Intent(this, Registration.class));
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
        infoTypes = findViewById(R.id.login_type);
        forgotPassword = findViewById(R.id.forgot_password);
        languages = findViewById(R.id.languages);
        buttons = new Button[]{findViewById(R.id.auth_send_link)};
        editTexts = new EditText[]{findViewById(R.id.info_for_link)};
        textViews = new TextView[]{findViewById(R.id.question), findViewById(R.id.forgot_info),
                findViewById(R.id.country_code), findViewById(R.id.email_name),
                findViewById(R.id.other_variant), findViewById(R.id.create_new_account)};
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

        AdapterView.OnItemSelectedListener itemLoginTypeSelectedListener = new AdapterView.OnItemSelectedListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (editTexts[0].getBackground() != resources.getDrawable(R.drawable.edit_text_auto_reg, getTheme()))
                    editTexts[0].setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg, getTheme()));
                if (editTexts[0].getText().length() > 0) editTexts[0].setText("");
                setLoginType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        infoTypes.setOnItemSelectedListener(itemLoginTypeSelectedListener);

        editTexts[0].addTextChangedListener(new Validator(editTexts[0]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[0].setTextColor(resources.getColor(R.color.white, getTheme()));

                String type = (String) infoTypes.getSelectedItem();

                try {
                    if (Objects.equals(type, "Phone number") || Objects.equals(type, "Номер телефону")) {
                        Validations.validatePhoneNumber(text, textViews[2].getText().toString(), resources);
                    } else if (Objects.equals(type, "Email")) {
                        Validations.validateEmail(text, textViews[3].getText().toString(), resources);
                    }
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, ForgotPassword.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, ForgotPassword.this.getTheme()));
                }
            }
        });

        buttons[0].setOnClickListener(v -> {
            if (editTexts[0].length() != 0) {
                try {
                    if (infoTypes.getSelectedItem() == "Phone number" || infoTypes.getSelectedItem() == "Номер телефону") {
                        Validations.validatePhoneNumber(editTexts[0].getText().toString(), textViews[2].getText().toString(), resources);
                        TransitUser.user.setPhoneNumber(editTexts[0].getText().toString());
                    } else if (infoTypes.getSelectedItem() == "Email") {
                        Validations.validateEmail(editTexts[0].getText().toString(), textViews[3].getText().toString(), resources);
                        TransitUser.user.setEmail(editTexts[0].getText().toString());
                    }

                    setValidationError(false, "");
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                setValidationError(true, resources.getString(R.string.error_send_password1));
            }
        });

        textViews[5].setOnClickListener(v -> {
            startActivity(Intents.getRegistration());
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

    // region Login types
    private void setLoginType(int position) {
        switch (position) {
            case 0:
                editTexts[0].setHint(resources.getString(R.string.login_hint_phone));
                editTexts[0].setInputType(InputType.TYPE_CLASS_PHONE);
                textViews[2].setVisibility(View.VISIBLE);
                textViews[3].setVisibility(View.GONE);
                break;
            case 1:
                editTexts[0].setHint(resources.getString(R.string.login_hint_email));
                editTexts[0].setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                textViews[2].setVisibility(View.GONE);
                textViews[3].setVisibility(View.VISIBLE);
                break;
        }
    }

    private void setLoginTypes() {
        ArrayAdapter<String> adapterForLoginTypes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        adapterForLoginTypes.add(resources.getString(R.string.login_hint_phone));
        adapterForLoginTypes.add(resources.getString(R.string.login_hint_email));

        adapterForLoginTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        infoTypes.setAdapter(adapterForLoginTypes);
        infoTypes.setSelection(0);
    }
    // endregion

    // region Localisation
    private void setStringResources() {
        setLoginTypes();
        setLoginType(infoTypes.getSelectedItemPosition());
        buttons[0].setText(resources.getString(R.string.get_login_link));

        textViews[0].setText(resources.getString(R.string.can_not_login));
        textViews[1].setText(resources.getString(R.string.forgot_pass_info));
        textViews[2].setText(resources.getString(R.string.code));
        textViews[3].setText(resources.getString(R.string.gmail));
        textViews[4].setText(resources.getString(R.string.or));
        textViews[5].setText(resources.getString(R.string.create_account));

        if (textViews[2].getVisibility() == View.VISIBLE) {
            editTexts[0].setHint(resources.getString(R.string.phone_reg));
        } else if (textViews[3].getVisibility() == View.VISIBLE) {
            editTexts[0].setHint(resources.getString(R.string.email_reg));
        }
    }
    // endregion
}