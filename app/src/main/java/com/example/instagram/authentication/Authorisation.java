package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.MyRetrofit;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.SendToCheckExistUser;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import org.json.JSONException;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Authorisation extends AppCompatActivity {
    private Resources resources;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion

    private Spinner loginTypes;
    private EditText[] editTexts;
    private Button[] buttons;
    private TextView[] textViews;
    private ImageView[] imageViews;
    private CheckBox rememberMe;
    private LinearLayout authorisation;
    private boolean passwordEyeState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorisation);

        findViews();
        setIntents();
        try {
            if (setRememberMe())
                return;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        UiVisibility.setUiVisibility(this);

        resources = getResources();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Localisation.setFirstLocale(languages);

        setLoginTypes();

        setListeners();
        setAnimations(authorisation).start();

        if (!TransitUser.user.getOtherInfo().isPhoneBookPermission()
                || !TransitUser.user.getOtherInfo().isMediaPermission())
            setPermissions();
    }

    private boolean setRememberMe() throws JSONException {
        boolean rememberMeFlag = com.example.instagram.services.SharedPreferences.loadBoolSP(this, "rememberMe");


        if (rememberMeFlag) {
            TransitUser.user.setNickName(com.example.instagram.services.SharedPreferences.loadStringSP(this, "nickName"));
            TransitUser.user.setPhoneNumber(com.example.instagram.services.SharedPreferences.loadStringSP(this, "phone"));
            TransitUser.user.setEmailCode(com.example.instagram.services.SharedPreferences.loadStringSP(this, "email"));
            TransitUser.user.setPassword(com.example.instagram.services.SharedPreferences.loadStringSP(this, "password"));

            Services.authorizeUser(new Callback<String>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.code() == 200) {
                        startActivity(Intents.getNewsList());
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "User are not authorized", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            });

            startActivity(Intents.getNewsList());
            finish();
            return true;
        }

        return false;
    }

    private AnimationDrawable setAnimations(View view) {
        AnimationDrawable animationDrawable = (AnimationDrawable) view.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        return animationDrawable;
    }

    private void setIntents() {
        Intents.setRegistration(new Intent(this, Registration.class));
        Intents.setForgotPassword(new Intent(this, ForgotPassword.class));
        Intents.setNewsList(new Intent(this, NewsLine.class));
    }

    @Override
    protected void onResume() {
        Localisation.setFirstLocale(languages);
        super.onResume();
    }

    private void setPermissions() {
        AlertDialog.Builder permissionsDialog = Permissions.getPermissionDialog(this, resources);
        permissionsDialog.setNegativeButton("Cancel", (dialog1, which) -> finish());
        permissionsDialog.create().show();
    }


    private void findViews() {
        authorisation = findViewById(R.id.authorisation);
        languages = findViewById(R.id.languages);
        loginTypes = findViewById(R.id.login_type);
        rememberMe = findViewById(R.id.remember_flag);
        editTexts = new EditText[]{findViewById(R.id.auth_login), findViewById(R.id.auth_pass)};
        buttons = new Button[]{findViewById(R.id.auth_eye), findViewById(R.id.auth_log_in)};
        textViews = new TextView[]{findViewById(R.id.auth_forgot_pass), findViewById(R.id.reg_question),
                findViewById(R.id.link_log_in), findViewById(R.id.country_code), findViewById(R.id.email_name),
                findViewById(R.id.remember_title)};
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

        AdapterView.OnItemSelectedListener itemLoginTypeSelectedListener = new AdapterView.OnItemSelectedListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (editTexts[0].getBackground() != resources.getDrawable(R.drawable.edit_text_auto_reg, getTheme()))
                    editTexts[0].setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg, getTheme()));
                if (editTexts[0].getText().length() > 0) editTexts[0].getText().clear();
                setLoginType(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        loginTypes.setOnItemSelectedListener(itemLoginTypeSelectedListener);

        buttons[0].setOnClickListener(v -> {
            passwordEyeState = !passwordEyeState;

            if (passwordEyeState) {
                editTexts[1].setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                buttons[0].setText(resources.getString(R.string.hide_password));
            } else {
                editTexts[1].setTransformationMethod(PasswordTransformationMethod.getInstance());
                buttons[0].setText(resources.getString(R.string.show_password));
            }
        });

        editTexts[0].addTextChangedListener(new Validator(editTexts[0]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[0].setTextColor(resources.getColor(R.color.white, getTheme()));

                String type = (String) loginTypes.getSelectedItem();

                try {
                    if (Objects.equals(type, "Phone number") || Objects.equals(type, "Номер телефону")) {
                        Validations.validatePhoneNumber(text, textViews[3].getText().toString(), resources);
                    } else if (Objects.equals(type, "Username") || Objects.equals(type, "Ім\\'я корисувача")) {
                        Validations.validateUserName(text, resources);
                    } else if (Objects.equals(type, "Email")) {
                        Validations.validateEmail(text, textViews[4].getText().toString(), resources);
                    }

                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, Authorisation.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, Authorisation.this.getTheme()));
                }
            }
        });

        editTexts[1].addTextChangedListener(new Validator(editTexts[1]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[1].setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validatePassword(text, resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, Authorisation.this.getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, Authorisation.this.getTheme()));
                }
            }
        });

        // authorisation
        buttons[1].setOnClickListener(v -> {
            if (editTexts[0].length() != 0 && editTexts[1].length() != 0) {
                try {
                    Validations.validatePassword(editTexts[1].getText().toString(), resources);

                    if (loginTypes.getSelectedItem().equals("Phone number") || loginTypes.getSelectedItem().equals("Номер телефону")) {
                        Validations.validatePhoneNumber(editTexts[0].getText().toString(), textViews[3].getText().toString(), resources);
                        TransitUser.user.setPhoneNumber(editTexts[0].getText().toString());
                    } else if (loginTypes.getSelectedItem().equals("Username") || loginTypes.getSelectedItem().equals("Ім\\'я корисувача")) {
                        Validations.validateUserName(editTexts[0].getText().toString(), resources);
                        TransitUser.user.setNickName(editTexts[0].getText().toString());
                    } else if (loginTypes.getSelectedItem().equals("Email")) {
                        Validations.validateEmail(editTexts[0].getText().toString(), textViews[4].getText().toString(), resources);
                        TransitUser.user.setEmail(editTexts[0].getText().toString());
                    }

                    TransitUser.user.setPassword(editTexts[1].getText().toString());

                    setValidationError(false, "");

                    if (rememberMe.isChecked()) {
                        com.example.instagram.services.SharedPreferences.saveSP(this, "rememberMe", rememberMe.isChecked());
                        com.example.instagram.services.SharedPreferences.saveSP(this, "nickName", TransitUser.user.getNickName());
                        com.example.instagram.services.SharedPreferences.saveSP(this, "phone", TransitUser.user.getPhoneNumber());
                        com.example.instagram.services.SharedPreferences.saveSP(this, "email", TransitUser.user.getEmail());
                        com.example.instagram.services.SharedPreferences.saveSP(this, "password", TransitUser.user.getPassword());
                    }
                    Services.authorizeUser(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            assert response.body() != null;
                            if (response.body().contains("0")) {
                                startActivity(Intents.getNewsList());
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "User are not authorized", Toast.LENGTH_SHORT).show();
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
            }
            else {
                Toast.makeText(this, resources.getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
            }
        });

        // forgot password
        textViews[0].setOnClickListener(v -> {
            if (TransitUser.user.getNickName() != null) {
                try {
                    Services.sendToForgotPassword(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            System.out.println(response.body());
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            System.out.println(t.getMessage());
                        }
                    });
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }

                startActivity(Intents.getForgotPassword());
            } else {
                Toast.makeText(getApplicationContext(), resources.getString(R.string.auth_attempt), Toast.LENGTH_SHORT).show();
            }
        });

        textViews[2].setOnClickListener(v -> startActivity(Intents.getRegistration()));
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
                editTexts[0].setHint(resources.getString(R.string.login_hint_username));
                editTexts[0].setInputType(InputType.TYPE_CLASS_TEXT);
                textViews[3].setVisibility(View.GONE);
                textViews[4].setVisibility(View.GONE);
                break;
            case 1:
                editTexts[0].setHint(resources.getString(R.string.login_hint_email));
                editTexts[0].setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                textViews[3].setVisibility(View.GONE);
                textViews[4].setVisibility(View.VISIBLE);
                break;
            case 2:
                editTexts[0].setHint(resources.getString(R.string.login_hint_phone));
                editTexts[0].setInputType(InputType.TYPE_CLASS_PHONE);
                textViews[3].setVisibility(View.VISIBLE);
                textViews[4].setVisibility(View.GONE);
                break;
        }
    }

    private void setLoginTypes() {
        //region Login types
        ArrayAdapter<String> adapterForLoginTypes = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        //adapterForLoginTypes.add(resources.getString(R.string.login_hint_phone));
        adapterForLoginTypes.add(resources.getString(R.string.login_hint_username));
        //adapterForLoginTypes.add(resources.getString(R.string.login_hint_email));

        adapterForLoginTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        loginTypes.setAdapter(adapterForLoginTypes);
        loginTypes.setSelection(0);
    }
    // endregion

    // region Localisation
    private void setStringResources() {
        setLoginTypes();
        setLoginType(loginTypes.getSelectedItemPosition());
        editTexts[1].setHint(resources.getString(R.string.password_hint));

        buttons[0].setText(resources.getString(R.string.show_password));
        buttons[1].setText(resources.getString(R.string.log_in));

        textViews[0].setText(resources.getString(R.string.password_forgot));
        textViews[1].setText(resources.getString(R.string.do_not_have_an_acc));
        textViews[2].setText(resources.getString(R.string.sing_in));
        textViews[3].setText(resources.getString(R.string.code));
        textViews[4].setText(resources.getString(R.string.gmail));
        textViews[5].setText(resources.getString(R.string.remember_me));
    }
    // endregion
}