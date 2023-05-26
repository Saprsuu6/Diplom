package com.example.instagram.main_process;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
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
import com.example.instagram.services.FindUser;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPosts;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPostsInCells;

import org.json.JSONException;

public class SelfPage extends AppCompatActivity {

    private Resources resources;
    private LinearLayout selfPage;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private ImageView[] imageViews;
    private TextView[] textViews;
    private ImageView[] imageViewsBottom;
    private PagingViewGetAllPostsInCells adapter;
    private FindUser findUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_page);
        setUiVisibility();

        setIntents();
        findViews();

        // region find users
        try {
            findUser = new FindUser(this, resources);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        // endregion
        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Localisation.setFirstLocale(languages);

        setListeners();

        registerForContextMenu(imageViews[2]);

        try {
            adapter = new PagingViewGetAllPostsInCells(findViewById(R.id.scroll_view),
                    findViewById(R.id.recycler_view), findViewById(R.id.skeleton),
                    this, this ,1, 10);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setIntents() {
        // TODO: set new intents
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        if (v.getId() == R.id.user_context) {
            inflater.inflate(R.menu.user_context, menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                System.out.println("settings"); // TODO: settings
                return true;
            case R.id.statistic:
                System.out.println("statistic"); // TODO: statistic
                return true;
            case R.id.archive:
                System.out.println("archive"); // TODO: archive
                return true;
            case R.id.saved:
                System.out.println("saved"); // TODO: saved
                return true;
            case R.id.payments:
                System.out.println("payments"); // TODO: payments
                return true;
            case R.id.favorites:
                System.out.println("favorites"); // TODO: favorites
                return true;
            case R.id.ur_contacts:
                System.out.println("ur_contacts"); // TODO: favorites
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private void findViews() {
        resources = getResources();
        selfPage = findViewById(R.id.self_page_activity);
        languages = findViewById(R.id.languages);
        imageViews = new ImageView[]{findViewById(R.id.go_to_the_news_line),
                findViewById(R.id.change_main_theme), findViewById(R.id.user_context)};
        textViews = new TextView[]{findViewById(R.id.nick_name),
                findViewById(R.id.user_name), findViewById(R.id.user_description),
                findViewById(R.id.amount_posts_title), findViewById(R.id.amount_posts),
                findViewById(R.id.amount_followings_title), findViewById(R.id.amount_followings),
                findViewById(R.id.amount_followers_title), findViewById(R.id.amount_followers)};
        imageViewsBottom = new ImageView[]{findViewById(R.id.home), findViewById(R.id.search),
                findViewById(R.id.add_post), findViewById(R.id.notifications), findViewById(R.id.self_page)};
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

        imageViews[0].setOnClickListener(v -> finish());

        // initialize menu bottom
        BottomMenu.setListeners(this,
                new ImageView[]{imageViewsBottom[1],
                        imageViewsBottom[2], imageViewsBottom[3]},
                findUser);

        // home
        imageViewsBottom[0].setOnClickListener(v -> finish());


        // self page
        imageViewsBottom[4].setOnClickListener(v -> {
            ((NestedScrollView) findViewById(R.id.scroll_view)).scrollTo(0, 0);
        });
    }

    // region Localisation
    private void setStringResources() {
        textViews[3].setText(resources.getString(R.string.posts));
        textViews[5].setText(resources.getString(R.string.followings));
        textViews[7].setText(resources.getString(R.string.followers));
    }
    // endregion
}