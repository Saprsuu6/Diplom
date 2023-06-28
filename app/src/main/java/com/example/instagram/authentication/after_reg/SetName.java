package com.example.instagram.authentication.after_reg;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
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

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.RegistrationActivities;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;

public class SetName extends AppCompatActivity {
    private class Views {
        private final LinearLayout setLoginLayout;
        private final TextView setLoginTitle;
        private final TextView setLoginDescription;
        private final EditText setLoginField;
        private final Button next;
        private final TextView haveAnAccount;
        private final TextView haveAnAccountLink;
        private final Spinner languagesSpinner;

        public Views() {
            setLoginLayout = findViewById(R.id.set_name);
            setLoginField = findViewById(R.id.info_for_name);
            next = findViewById(R.id.let_name_next);
            setLoginDescription = findViewById(R.id.let_name_info);
            setLoginTitle = findViewById(R.id.let_info);
            haveAnAccount = findViewById(R.id.reg_question);
            haveAnAccountLink = findViewById(R.id.link_log_in);
            languagesSpinner = findViewById(R.id.languages);
        }
    }

    private Resources resources;
    //private LinearLayout setName;
    private Localisation localisation;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_name);

        resources = getResources();
        views = new Views();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
        Animation.getAnimations(views.setLoginLayout).start();
    }

    private void setIntents() {
        if (Intents.getSetPassword() == null)
            Intents.setSetPassword(new Intent(this, SetPassword.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
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

        views.next.setOnClickListener(v -> {
            if (views.setLoginField.length() != 0) {
                TransitUser.user.setLogin(views.setLoginField.getText().toString().trim());
                RegistrationActivities.activityList.add(this);
                startActivity(Intents.getSetPassword());
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
    private void setStringResources() {
        views.next.setText(resources.getString(R.string.next_step));
        views.setLoginTitle.setText(resources.getString(R.string.let_name));
        views.setLoginDescription.setText(resources.getString(R.string.let_name_info));
        views.haveAnAccount.setText(resources.getString(R.string.have_an_acc));
        views.haveAnAccountLink.setText(resources.getString(R.string.log_in));
        views.setLoginField.setHint(resources.getString(R.string.name));
    }

    // endregion
}