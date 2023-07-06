package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.UiVisibility;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SetBirthday extends AppCompatActivity {
    private class Views {
        private final TextView setBirthdayTitle;
        private final EditText setBirthdayField;
        private final Button next;
        private final TextView haveAnAccount;
        private final TextView haveAnAccountLink;
        private final Spinner languagesSpinner;

        public Views() {
            setBirthdayTitle = findViewById(R.id.let_info);
            next = findViewById(R.id.next);
            setBirthdayField = findViewById(R.id.birth_date);
            haveAnAccount = findViewById(R.id.reg_question);
            haveAnAccountLink = findViewById(R.id.link_log_in);
            languagesSpinner = findViewById(R.id.languages);
        }
    }

    public static final int yearOffset = 13;
    private Resources resources;
    private Localisation localisation;
    private DatePickerDialog.OnDateSetListener setListener;
    private Calendar selectedDate;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_birthday);

        views = new Views();
        resources = getResources();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());
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
        Localisation.setFirstLocale(views.languagesSpinner);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();

                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
                if (selectedDate != null) {
                    views.setBirthdayField.setText(DateFormatting.formatDate(selectedDate));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

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
                            Cache.deleteSP(getApplicationContext(), CacheScopes.USER_PASSWORD.toString());
                            Cache.deleteSP(getApplicationContext(), CacheScopes.USER_PASSWORD_REPEAT.toString());
                        }, this, new Object[]{jsonObject}).sinUp();

                    } catch (JSONException | IOException e) {
                        throw new RuntimeException(e);
                    }

                    startActivity(Intents.getSetAvatar());
                    RegistrationActivities.deleteActivities();
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

    // region Localisation
    @SuppressLint("SetTextI18n")
    private void setStringResources() {
        views.next.setText(resources.getString(R.string.next_step));
        views.setBirthdayTitle.setText(resources.getString(R.string.let_birthday));
        views.haveAnAccount.setText(resources.getString(R.string.have_an_acc));
        views.haveAnAccountLink.setText(resources.getString(R.string.log_in));
        views.setBirthdayField.setHint(resources.getString(R.string.birthday_hint));
        if (selectedDate != null) {
            views.setBirthdayField.setText(views.setBirthdayField.getText() + ": " + DateFormatting.formatDate(selectedDate));
        }
    }
    // endregion
}