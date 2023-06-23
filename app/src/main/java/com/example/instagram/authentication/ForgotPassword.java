package com.example.instagram.authentication;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.R;
import com.example.instagram.services.Errors;
import com.example.instagram.services.Services;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.TransitUser;

import org.json.JSONException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    private Handler handler;
    private Runnable runnable;

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

        //setLoginTypes();

        setListeners();
        setAnimations(forgotPassword).start();

        handler = new Handler();
        runnable = checkUsedLink();
        handler.postDelayed(runnable, 5000L);
    }

    private Runnable checkUsedLink() {
        return () -> {
            try {
                Services.sendToCheckUsedLickInMail(new Callback<>() {
                    @Override
                    public void onResponse(@Nullable Call<String> call, @Nullable Response<String> response) {
                        assert response != null;
                        assert response.body() != null;

                        if (response.body().contains("0")) {
                            String responseStr = response.body();
                            int index = responseStr.indexOf(":");
                            responseStr = responseStr.substring(index + 1);

                            TransitUser.user.setEmailCode(responseStr.trim());
                            startActivity(Intents.getCreateNewPassword());
                            handler.removeCallbacks(runnable);
                            finish();
                        } else {
                            handler.postDelayed(runnable, 5000L);
                        }
                    }

                    @Override
                    public void onFailure(@Nullable Call<String> call, @Nullable Throwable t) {
                        assert t != null;
                        System.out.println(t.getMessage());
                    }
                });

            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        };
    }

    private void setIntents() {
        Intents.setRegistration(new Intent(this, Registration.class));
        Intents.setCreateNewPassword(new Intent(this, CreateNewPassword.class));
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
        infoTypes = findViewById(R.id.login_type);
        forgotPassword = findViewById(R.id.forgot_password);
        languages = findViewById(R.id.languages);
        buttons = new Button[]{findViewById(R.id.confirm_code_button)};
        editTexts = new EditText[]{findViewById(R.id.code)};
        textViews = new TextView[]{findViewById(R.id.question), findViewById(R.id.forgot_info)};
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

        buttons[0].setOnClickListener(v -> {
            if (editTexts[0].length() != 0) {
                try {
                    TransitUser.user.setEmailCode(editTexts[0].getText().toString());

                    Services.sendToCheckUserCode(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                            String strResponse = response.body();
                            assert strResponse != null;
                            assert response.body() != null;

                            Errors.enterCode(getApplicationContext(), response.body()).show();

                            if (strResponse.contains("1")) {
                                char symbol = strResponse.charAt(strResponse.length() - 1);
                                int attempts = Integer.parseInt(String.valueOf(symbol));

                                if (attempts > 0) {
                                    Toast.makeText(getApplicationContext(), attempts, Toast.LENGTH_SHORT).show();
                                } else {
                                    startActivity(Intents.getAuthorisation());
                                    finish();
                                }
                            }

                            String responseStr = response.body();
                            int index = responseStr.indexOf(":");
                            responseStr = responseStr.substring(index + 1);

                            TransitUser.user.setEmailCode(responseStr.trim());

                            startActivity(Intents.getCreateNewPassword());
                            finish();
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            System.out.println("Error: " + t.getMessage());
                        }
                    }, TransitUser.user.getEmailCode());
                } catch (Exception exception) {
                    setValidationError(true, exception.getMessage());
                }
            } else {
                setValidationError(true, resources.getString(R.string.error_send_password1));
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
        //setLoginTypes();
        //setLoginType(infoTypes.getSelectedItemPosition());
        buttons[0].setText(resources.getString(R.string.confirm_code));

        textViews[0].setText(resources.getString(R.string.can_not_login));
        textViews[1].setText(resources.getString(R.string.confirm_code_text));
//        textViews[2].setText(resources.getString(R.string.code));
//        textViews[3].setText(resources.getString(R.string.gmail));

        editTexts[0].setHint(resources.getString(R.string.mail_code));
//        if (textViews[2].getVisibility() == View.VISIBLE) {
//            editTexts[0].setHint(resources.getString(R.string.phone_reg));
//        } else if (textViews[3].getVisibility() == View.VISIBLE) {
//            editTexts[0].setHint(resources.getString(R.string.email_reg));
//        }
    }
    // endregion
}