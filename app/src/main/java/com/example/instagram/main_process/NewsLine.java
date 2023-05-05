package com.example.instagram.main_process;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.pagination.paging_views.PagingViewPosts;
import com.example.instagram.services.themes_and_backgrounds.Backgrounds;
import com.example.instagram.services.themes_and_backgrounds.Themes;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import java.util.ArrayList;
import java.util.List;

public class NewsLine extends AppCompatActivity {
    private LinearLayout newsLine;
    private Resources resources;
    private ImageView[] imageViewsTop;
    private ImageView[] imageViewsBottom;
    private PagingViewPosts pagingView;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    public static List<TextView> textViews = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_line);
        setUiVisibility();

        setIntents();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Localisation.setFirstLocale(languages);

        setListeners();

        pagingView = new PagingViewPosts(findViewById(R.id.scroll_view),
                findViewById(R.id.recycler_view), findViewById(R.id.skeleton),
                this, 1, 10);
    }

    private void setIntents() {
        Intents.setComments(new Intent(this, Comments.class));
        Intents.setSelfPage(new Intent(this, SelfPage.class));
        Intents.setChatList(new Intent(this, ChatList.class));
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onResume() {
        ThemesBackgrounds.setThemeContent(resources, imageViewsTop[1], getApplicationContext());
        AppCompatDelegate.setDefaultNightMode(ThemesBackgrounds.theme.getValue());
        ThemesBackgrounds.loadBackground(this, newsLine);

        setAnimations();
        //Localisation.setFirstLocale(languages);

        super.onResume();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        if (v.getId() == R.id.post_context) {
            inflater.inflate(R.menu.post_context_menu, menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_post:
                System.out.println("remove post"); // TODO: remove post
                return true;
            case R.id.complain_post:
                System.out.println("complain post"); // TODO: complain post
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pagingView.savePosition();
    }

    private void setAnimations() {
        if (ThemesBackgrounds.background == Backgrounds.Background0.getValue()) {
            AnimationDrawable animationDrawable = (AnimationDrawable) newsLine.getBackground();
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
        newsLine = findViewById(R.id.news_list);
        languages = findViewById(R.id.languages);
        imageViewsTop = new ImageView[]{findViewById(R.id.logo), findViewById(R.id.change_main_theme), findViewById(R.id.change_theme), findViewById(R.id.direct)};
        imageViewsBottom = new ImageView[]{findViewById(R.id.home), findViewById(R.id.search), findViewById(R.id.add_post), findViewById(R.id.notifications), findViewById(R.id.self_page)};
    }

    private void chooseTheme() {
        @SuppressLint("ResourceType") AlertDialog.Builder permissionsDialog = ThemesBackgrounds.getThemeDialog(this, resources, this, newsLine);
        permissionsDialog.setNegativeButton("Cancel", null);
        permissionsDialog.create().show();
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

        imageViewsBottom[4].setOnClickListener(v -> {
            Intent intent = Intents.getSelfPage();
            startActivity(intent);
        });

        // home
        imageViewsBottom[0].setOnClickListener(v -> {
            ((NestedScrollView) findViewById(R.id.scroll_view)).scrollTo(0, 0);
        });

        // direct
        imageViewsTop[3].setOnClickListener(v -> {
            Intent intent = Intents.getChatList();
            startActivity(intent);
        });

        // TODO: search, add_post, notifications

        // set system theme on label
        imageViewsTop[0].setOnClickListener(v -> {
            ThemesBackgrounds.theme = Themes.SYSTEM;
            recreate();
        });

        // set night/day theme
        imageViewsTop[1].setOnClickListener(v -> {
            ThemesBackgrounds.theme = ThemesBackgrounds.isNight(resources)
                    ? Themes.DAY : Themes.NIGHT;
            recreate();
        });

        // choose background
        imageViewsTop[2].setOnClickListener(v -> {
            chooseTheme();
        });
    }

    // region Localisation
    private void setStringResources() {
        if (NewsLine.textViews.size() > 0) {
            NewsLine.textViews.get(0).setText(resources.getString(R.string.amount_likes_title));
            NewsLine.textViews.get(1).setText(resources.getString(R.string.hours_ago));
        }
    }
    // endregion
}