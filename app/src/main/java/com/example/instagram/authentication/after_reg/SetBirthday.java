package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.Resources;
import com.example.instagram.services.UiVisibility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SetBirthday extends AppCompatActivity {
    private class Views {
        private final EditText setBirthdayField;
        private final Button next;
        private final TextView haveAnAccountLink;

        public Views() {
            next = findViewById(R.id.next);
            setBirthdayField = findViewById(R.id.birth_date);
            haveAnAccountLink = findViewById(R.id.link_log_in);
        }
    }

    public static final int yearOffset = 13;
    private DatePickerDialog.OnDateSetListener setListener;
    private Calendar selectedDate;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_birthday);

        views = new Views();
        views.setBirthdayField.setInputType(InputType.TYPE_NULL);

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    private void setIntents() {
        if (Intents.getSetAvatar() == null) Intents.setSetAvatar(new Intent(this, SetAvatar.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStringResources(); // TODO set resources
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        views.setBirthdayField.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(SetBirthday.this, android.R.style.Theme_DeviceDefault_Dialog, setListener, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        setListener = (view, year1, month1, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());

            if (year - yearOffset <= year1) {
                Toast.makeText(this, R.string.birthday_error_age, Toast.LENGTH_SHORT).show();
            } else {
                views.setBirthdayField.setText(DateFormatting.formatDate(selectedDate));
            }
        };

        views.next.setOnClickListener(v -> {
            if (views.setBirthdayField.length() != 0) {
                if (year <= selectedDate.get(1) - yearOffset) {
                    Toast.makeText(this, R.string.birthday_error_age, Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
                        String password = Cache.loadStringSP(this, CacheScopes.USER_PASSWORD.toString());
                        String email = Cache.loadStringSP(this, CacheScopes.USER_EMAIL.toString());
                        Date birthday = selectedDate.getTime();

                        JSONObject jsonObject = User.getJSONToRegistration(login, password, email, birthday);

                        new DoCallBack().setValues(() -> {
                            // delete unnecessary
                            Cache.deleteSP(this, CacheScopes.USER_PASSWORD.toString());
                            Cache.deleteSP(this, CacheScopes.USER_PASSWORD_REPEAT.toString());
                            startActivity(Intents.getAuthorisation());
                            finish();
                        }, this, new Object[]{jsonObject, (Runnable) () -> {
                            Cache.deleteAppSP(SetBirthday.this);
                            startActivity(Intents.getSetAvatar());
                            RegistrationActivities.deleteActivities();
                        }}).sendToSinUp();

                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                Resources.getToast(this, this.getResources().getString(R.string.error_send_password1)).show();
            }
        });

        views.haveAnAccountLink.setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
            RegistrationActivities.deleteActivities();
        });
    }

    // region Localisation
    @SuppressLint("SetTextI18n")
    private void setStringResources() {
        if (selectedDate != null) {
            Resources.setText(views.setBirthdayField.getText() + ": " + DateFormatting.formatDate(selectedDate), views.setBirthdayField);
        }
    }
    // endregion
}