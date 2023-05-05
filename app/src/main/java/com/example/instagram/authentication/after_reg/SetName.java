package com.example.instagram.authentication.after_reg;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
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
import androidx.appcompat.app.AppCompatDelegate;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.TransitUser;

import java.util.Locale;
import java.util.Objects;

public class SetName extends AppCompatActivity {
    private Resources resources;
    private LinearLayout setName;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private EditText[] editTexts;
    private Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);
        setUiVisibility();

        resources = getResources();
        setIntents();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Localisation.setFirstLocale(languages);

        setListeners();
        setAnimations();
    }

    private void setIntents() {
        if (Intents.getSetPassword() == null)
            Intents.setSetPassword(new Intent(this, SetPassword.class));

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
        setName = findViewById(R.id.set_name);
        languages = findViewById(R.id.languages);
        editTexts = new EditText[]{findViewById(R.id.info_for_name)};
        buttons = new Button[]{findViewById(R.id.let_name_next)};
        textViews = new TextView[]{findViewById(R.id.let_info),
                findViewById(R.id.let_name_info), findViewById(R.id.reg_question),
                findViewById(R.id.link_log_in)};
    }

    private void setAnimations() {
        AnimationDrawable animationDrawable = (AnimationDrawable) setName.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
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
                TransitUser.user.setName(editTexts[0].getText().toString().trim());

                startActivity(Intents.getSetPassword());
            } else {
                Toast.makeText(this, resources.getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
            }
        });

        textViews[3].setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
        });
    }

    // region Localisation
    private void setStringResources() {
        buttons[0].setText(resources.getString(R.string.next_step));

        textViews[0].setText(resources.getString(R.string.let_name));
        textViews[1].setText(resources.getString(R.string.let_name_info));
        textViews[2].setText(resources.getString(R.string.have_an_acc));
        textViews[3].setText(resources.getString(R.string.log_in));

        editTexts[0].setHint(resources.getString(R.string.name));
    }

    // endregion
}