package com.example.instagram.main_process;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.themes_and_backgrounds.Backgrounds;
import com.example.instagram.services.themes_and_backgrounds.Themes;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

public class Comments extends AppCompatActivity {
    private Resources resources;
    private LinearLayout comments;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private EditText[] editTexts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setUiVisibility();

        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Localisation.setFirstLocale(languages);

        setListeners();
        setAnimations();

//        pagingView = new PagingViewPosts(findViewById(R.id.scroll_view),
//                findViewById(R.id.recycler_view), findViewById(R.id.skeleton),
//                this, 1, 10);
    }

    private void setAnimations() {
        if (ThemesBackgrounds.background == Backgrounds.Background0.getValue()) {
            AnimationDrawable animationDrawable = (AnimationDrawable) comments.getBackground();
            animationDrawable.setExitFadeDuration(4000);
            animationDrawable.start();
        }
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void findViews() {
        resources = getResources();
        comments = findViewById(R.id.comments);
        languages = findViewById(R.id.languages);
        textViews = new TextView[]{findViewById(R.id.comment_title)};
        editTexts = new EditText[]{findViewById(R.id.comment_add)};
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
    }

    // region Localisation
    private void setStringResources() {
        textViews[0].setText(resources.getString(R.string.comments_title));
        editTexts[0].setHint(resources.getString(R.string.add_comment));
    }
    // endregion
}