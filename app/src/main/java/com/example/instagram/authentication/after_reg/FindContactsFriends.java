package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.pagination.paging_views.PagingViewFindUsers;


import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class FindContactsFriends extends AppCompatActivity {
    private Resources resources;
    private LinearLayout findContactsFriends;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    public static List<CheckBox> buttons = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_contacts_frieds);
        setUiVisibility();

        resources = getResources();
        setIntents();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());
        Localisation.setFirstLocale(languages);

        setListeners();
        setAnimations();

        try {
            PagingViewFindUsers pagingView = new PagingViewFindUsers(findViewById(R.id.scroll_view),
                    findViewById(R.id.recycler_view), findViewById(R.id.skeleton),
                    this, this, 1, 20);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        Localisation.setFirstLocale(languages);
        super.onResume();
    }

    private void setIntents() {
        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setAnimations() {
        AnimationDrawable animationDrawable = (AnimationDrawable) findContactsFriends.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    private void findViews() {
        findContactsFriends = findViewById(R.id.find_contacts_friends);
        languages = findViewById(R.id.languages);
        textViews = new TextView[]{findViewById(R.id.subscribe_header),
                findViewById(R.id.skip)};
    }

    @SuppressLint("ClickableViewAccessibility")
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

        textViews[1].setOnClickListener(v -> {
            // TODO start next activity
            finish();
        });
    }

    // region Localisation
    private void setStringResources() {
        textViews[0].setText(resources.getString(R.string.add_contacts_header));
        textViews[1].setText(resources.getString(R.string.add_btn_skip));

        for (CheckBox button : FindContactsFriends.buttons) {
            button.setText(!button.isChecked()
                    ? resources.getString(R.string.subscribe_btn)
                    : resources.getString(R.string.unsubscribe_btn));
        }
    }
    // endregion
}