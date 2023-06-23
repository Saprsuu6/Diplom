package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.R;
import com.example.instagram.authentication.after_reg.SetName;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.Validations;
import com.example.instagram.services.GetEthernetInfo;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.Validator;

import java.util.Objects;

public class Registration extends AppCompatActivity {
    private Resources resources;
    private LinearLayout registration;
    private EditText[] editTexts;
    private Button[] buttons;
    private TextView[] textViews;
    private CheckBox[] checkBoxes;
    private ImageView[] imageViews;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private String infoType;
    private CheckBox rememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgistration);

        setIntents();
        findViews();
        setRememberMe();

        setUiVisibility();
        resources = getResources();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        setListeners();
        toggleContentEmail();
        //toggleContentPhoneNumber();
        setAnimations(registration).start();
    }

    private void setIntents() {
        if (Intents.getSetName() == null) {
            Intents.setSetName(new Intent(this, SetName.class));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(languages);
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setRememberMe() {
        boolean rememberMeFlag = com.example.instagram.services.SharedPreferences.loadBoolSP(this, "rememberMe");
        rememberMe.setChecked(rememberMeFlag);
    }

    private void findViews() {
        registration = findViewById(R.id.registration);
        languages = findViewById(R.id.languages);
        rememberMe = findViewById(R.id.remember_flag);
        editTexts = new EditText[]{findViewById(R.id.phone_or_email)};
        buttons = new Button[]{findViewById(R.id.reg_log_in)};
        checkBoxes = new CheckBox[]{findViewById(R.id.receive_news_letters), findViewById(R.id.hide_phone), findViewById(R.id.hide_email)};
        textViews = new TextView[]{findViewById(R.id.reg_phone), findViewById(R.id.reg_email), findViewById(R.id.reg_question), findViewById(R.id.link_log_in), findViewById(R.id.country_code), findViewById(R.id.email_name), findViewById(R.id.remember_title)};
        imageViews = new ImageView[]{findViewById(R.id.validation_error)};
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

        rememberMe.setOnCheckedChangeListener((buttonView, isChecked) -> com.example.instagram.services.SharedPreferences.saveSP(this, "rememberMe", isChecked));

        textViews[0].setOnClickListener(v -> {
            setDefaultTextBoxSettings(editTexts[0]);
            toggleContentPhoneNumber();
        });

        textViews[1].setOnClickListener(v -> {
            setDefaultTextBoxSettings(editTexts[0]);
            toggleContentEmail();
        });

        buttons[0].setOnClickListener(v -> {
            //ArrayList<PhoneBookContact> contacts = Contacts.getContacts(this);

            if (editTexts[0].length() != 0) {
                try {
                    setValidationError(false, "");

                    switch (infoType) {
                        case "phone":
                            Validations.validatePhoneNumber(editTexts[0].getText().toString(), textViews[4].getText().toString(), resources);
                            TransitUser.user.setPhoneNumber(textViews[4].getText() + editTexts[0].getText().toString());
                            break;
                        case "email":
                            Validations.validateEmail(editTexts[0].getText().toString(), textViews[5].getText().toString(), resources);
                            TransitUser.user.setEmail(editTexts[0].getText().toString() + textViews[5].getText());
                            break;
                    }

                    TransitUser.user.getOtherInfo().setReceiveNewsletters(checkBoxes[0].isChecked());
                    TransitUser.user.getOtherInfo().setHidePhone(checkBoxes[1].isChecked());
                    TransitUser.user.getOtherInfo().setHideEmail(checkBoxes[2].isChecked());
                    TransitUser.user.getOtherInfo().setIpAddress(GetEthernetInfo.getNetworkInfo());

                    RegistrationActivities.activityList.add(this);
                    startActivity(Intents.getSetName());
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                setValidationError(true, resources.getString(R.string.error_send_password1));
            }
        });

        textViews[3].setOnClickListener(v -> finish());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void toggleContentPhoneNumber() {
        editTexts[0].setHint(resources.getString(R.string.phone_reg));
        textViews[5].setVisibility(View.GONE);
        textViews[4].setVisibility(View.VISIBLE);

        editTexts[0].setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg, getTheme()));
        editTexts[0].setInputType(InputType.TYPE_CLASS_PHONE);
        editTexts[0].addTextChangedListener(new Validator(editTexts[0]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[0].setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validatePhoneNumber(text, textViews[4].getText().toString(), resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, Registration.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, Registration.this.getTheme()));
                }
            }
        });

        infoType = "phone";
    }

    private void toggleContentEmail() {
        editTexts[0].setHint(resources.getString(R.string.email_reg));

        textViews[4].setVisibility(View.GONE);
        textViews[5].setVisibility(View.VISIBLE);

        editTexts[0].setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        editTexts[0].addTextChangedListener(new Validator(editTexts[0]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[0].setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validateEmail(text, textViews[5].getText().toString(), resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, Registration.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, Registration.this.getTheme()));
                }
            }
        });

        infoType = "email";
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setDefaultTextBoxSettings(EditText editText) {
        editText.getText().clear();
        editText.setTextColor(resources.getColor(R.color.black, getTheme()));
        editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg, getTheme()));
    }

    private AnimationDrawable setAnimations(View view) {
        AnimationDrawable animationDrawable = (AnimationDrawable) view.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        return animationDrawable;
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
        buttons[0].setText(resources.getString(R.string.sing_in));

        textViews[0].setText(resources.getString(R.string.phone_reg));
        textViews[1].setText(resources.getString(R.string.email_reg));
        textViews[2].setText(resources.getString(R.string.have_an_acc));
        textViews[3].setText(resources.getString(R.string.log_in));
        textViews[4].setText(resources.getString(R.string.code));

        if (textViews[4].getVisibility() == View.VISIBLE) {
            editTexts[0].setHint(resources.getString(R.string.phone_reg));
        } else if (textViews[5].getVisibility() == View.VISIBLE) {
            editTexts[0].setHint(resources.getString(R.string.email_reg));
        }

        textViews[6].setText(resources.getString(R.string.remember_me));

        checkBoxes[0].setText(resources.getString(R.string.check_receive_news_letters));
        checkBoxes[1].setText(resources.getString(R.string.check_hide_phone));
        checkBoxes[2].setText(resources.getString(R.string.check_hide_email));
    }
    // endregion
}