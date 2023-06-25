package com.example.instagram.authentication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.R;
import com.example.instagram.authentication.after_reg.SetName;
import com.example.instagram.main_process.SelfPage;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Errors;
import com.example.instagram.services.GetEthernetInfo;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Registration extends AppCompatActivity {
    public class Views {
        private final LinearLayout registrationLayout;
        private final EditText fieldToEmail;
        private final EditText fieldConfirmCode;
        private final Button confirmCode;
        private final Button singIn;
        private final ImageView warning;
        private final CheckBox receiveEmail;
        private final CheckBox hideEmail;
        private final CheckBox rememberMe;
        private final TextView haveAnAccount;
        private final TextView emailName;
        private final TextView haveAnAccountLink;
        private final Spinner languagesSpinner;

        public Views() {
            registrationLayout = findViewById(R.id.registration);
            fieldToEmail = findViewById(R.id.phone_or_email);
            fieldConfirmCode = findViewById(R.id.info_for_email_code);
            confirmCode = findViewById(R.id.confirm_code_button);
            singIn = findViewById(R.id.reg_log_in);
            receiveEmail = findViewById(R.id.receive_news_letters);
            hideEmail = findViewById(R.id.hide_email);
            rememberMe = findViewById(R.id.remember_flag);
            haveAnAccount = findViewById(R.id.reg_question);
            haveAnAccountLink = findViewById(R.id.link_log_in);
            languagesSpinner = findViewById(R.id.languages);
            warning = findViewById(R.id.validation_error);
            emailName = findViewById(R.id.email_name);
        }
    }

    private Resources resources;
    private Localisation localisation;
    private Views views;
    private Handler handler;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rgistration);

        handler = new Handler();
        runnable = checkUsedLink();
        views = new Views();
        resources = getResources();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        setRememberMe();
        setUiVisibility();
        setIntents();
        setListeners();

        Animation.getAnimations(views.registrationLayout).start();
    }

    private void setIntents() {
        if (Intents.getSetName() == null) {
            Intents.setSetName(new Intent(this, SetName.class));
        }
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

    private void setRememberMe() {
        boolean rememberMeFlag = com.example.instagram.services.SharedPreferences.loadBoolSP(this, "rememberMe");
        views.rememberMe.setChecked(rememberMeFlag);
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

        views.rememberMe.setOnCheckedChangeListener((buttonView, isChecked) -> com.example.instagram.services.SharedPreferences.saveSP(this, "rememberMe", isChecked));

        views.fieldToEmail.addTextChangedListener(new Validator(views.fieldToEmail) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editText.setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validateEmail(text, views.emailName.getText().toString(), resources);
                    setValidationError(false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()));
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()));
                }
            }
        });

        views.singIn.setOnClickListener(v -> {
            if (views.fieldToEmail.length() != 0) {
                try {
                    setValidationError(false, "");

                    TransitUser.user.getOtherInfo().setReceiveNewsletters(views.receiveEmail.isChecked());
                    TransitUser.user.getOtherInfo().setHideEmail(views.hideEmail.isChecked());
                    TransitUser.user.getOtherInfo().setIpAddress(GetEthernetInfo.getNetworkInfo());
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                setValidationError(true, resources.getString(R.string.error_send_password1));
            }
        });

        views.haveAnAccountLink.setOnClickListener(v -> finish());

        // send code to email
        views.singIn.setOnClickListener(v -> {
            if (views.warning.getVisibility() == View.GONE) {
                JSONObject body = new JSONObject();
                try {
                    body.put("login", SelfPage.userPage.getLogin());
                    body.put("newEmail", views.fieldToEmail.getText().toString());
                    body.put("token", TransitUser.user.getToken());

                    Services.sendToSendCodeForEmail(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Errors.forgotPasswordCode(getApplicationContext(), response.body()).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToSendCodeForEmail: (onFailure) ", t.getMessage());
                        }
                    }, body.toString());
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            }
        });

        // check code
        views.confirmCode.setOnClickListener(v -> {
            if (!RegistrationActivities.activityList.contains(this)) {
                RegistrationActivities.activityList.add(this);
            }

            if (views.fieldConfirmCode.getText().length() > 0) {

                JSONObject body = new JSONObject();
                try {
                    body.put("token", TransitUser.user.getToken());
                    body.put("code", views.confirmCode.getText());
                    body.put("newEmail", views.fieldToEmail.getText().toString());

                    Services.sendToChangeEmailFinally(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Errors.emailCodes(getApplicationContext(), response.body()).show();
                                startActivity(Intents.getSetName());
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToSendCodeForEmail: (onFailure) ", t.getMessage());
                        }
                    }, body.toString());
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            }
        });
    }

    private Runnable checkUsedLink() {
        return () -> {
            try {
                Services.sendToCheckUsedLickInMail(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().contains("0")) {
                                String responseStr = response.body();
                                int index = responseStr.indexOf(":");
                                responseStr = responseStr.substring(index + 1);
                                views.fieldConfirmCode.setText(responseStr.trim());
                            } else {
                                handler.postDelayed(runnable, 5000L);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });

            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        };
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
        views.haveAnAccountLink.setText(resources.getString(R.string.log_in));
        views.haveAnAccount.setText(resources.getString(R.string.have_an_acc));
        views.receiveEmail.setText(resources.getString(R.string.check_receive_news_letters));
        views.hideEmail.setText(resources.getString(R.string.check_hide_email));
        views.rememberMe.setText(resources.getString(R.string.remember_me));
        views.singIn.setText(resources.getString(R.string.sing_in));
        views.fieldToEmail.setHint(resources.getString(R.string.email_reg));
        views.fieldConfirmCode.setHint(resources.getString(R.string.confirm_code));
        views.confirmCode.setHint(resources.getString(R.string.confirm_code));
    }
}