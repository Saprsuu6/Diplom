package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.Services;

import org.json.JSONException;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

public class SetBirthday extends AppCompatActivity {
    private Resources resources;
    private LinearLayout setBirthday;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private EditText[] editTexts;
    private Button[] buttons;
    private DatePickerDialog.OnDateSetListener setListener;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_birthday);
        setUiVisibility();

        resources = getResources();
        setIntents();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());
        Localisation.setFirstLocale(languages);

        editTexts[0].setInputType(InputType.TYPE_NULL);

        setAnimations();
        setListeners();
    }

    private void setIntents() {
        if (Intents.getSetAvatar() == null)
            Intents.setSetAvatar(new Intent(this, SetAvatar.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
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
        setBirthday = findViewById(R.id.set_birthday);
        languages = findViewById(R.id.languages);
        editTexts = new EditText[]{findViewById(R.id.birth_date)};
        buttons = new Button[]{findViewById(R.id.next)};
        textViews = new TextView[]{findViewById(R.id.let_info),
                findViewById(R.id.reg_question),
                findViewById(R.id.link_log_in)};
    }

    private void setAnimations() {
        AnimationDrawable animationDrawable = (AnimationDrawable) setBirthday.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();

                if (selectedDate != null) {
                    DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
                    editTexts[0].setText(DateFormatting.formatDate(selectedDate));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        editTexts[0].setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    SetBirthday.this,
                    android.R.style.Theme_DeviceDefault_Dialog,
                    setListener, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        setListener = (view, year1, month1, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());

            if (year1 > year) {
                Toast.makeText(this, R.string.birthday_error, Toast.LENGTH_SHORT).show();
            } else {
                editTexts[0].setText(DateFormatting.formatDate(selectedDate));
                editTexts[0].setTextColor(resources.getColor(R.color.black, getTheme()));
            }
        };

        buttons[0].setOnClickListener(v -> {
            if (editTexts[0].length() != 0) {
                TransitUser.user.setBirthday(selectedDate.getTime());

                // region send data to server
                Thread thread = new Thread(){
                    public void run(){
                        try {
                            Services.addUser(TransitUser.user);
                        } catch (JSONException exception) {
                            Log.e("JSONException", exception.getMessage());
                        } catch (IOException exception) {
                            Log.e("IOException", exception.getMessage());
                        }
                    }
                };

                thread.start();
                //endregion

                startActivity(Intents.getSetAvatar());
            } else {
                Toast.makeText(this, resources.getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
            }
        });

        textViews[2].setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
        });
    }

    // region Localisation
    private void setStringResources() {
        buttons[0].setText(resources.getString(R.string.next_step));

        textViews[0].setText(resources.getString(R.string.let_birthday));
        textViews[1].setText(resources.getString(R.string.have_an_acc));
        textViews[2].setText(resources.getString(R.string.log_in));

        editTexts[0].setHint(resources.getString(R.string.birthday_hint));
    }
    // endregion
}