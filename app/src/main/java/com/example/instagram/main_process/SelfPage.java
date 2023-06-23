package com.example.instagram.main_process;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.FindUser;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.pagination.PaginationCurrentForAllPosts;
import com.example.instagram.services.pagination.PaginationCurrentForAllPostsInCells;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPosts;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPostsInCells;

import org.json.JSONException;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfPage extends AppCompatActivity {
    public static User userPage;
    private Resources resources;
    private LinearLayout selfPage;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private ImageView[] imageViews;
    private TextView[] textViews;
    private ImageView[] imageViewsBottom;
    private PagingViewGetAllPostsInCells pagingView;
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

        setListeners();
        registerForContextMenu(imageViews[2]);
        LoadAvatar();

        try {
            pagingView = new PagingViewGetAllPostsInCells(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, this);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(languages);
    }

    private void LoadAvatar() {
        // region send request to get avatar
        try {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avaLink = response.body();

                        // set ava
                        Glide.with(getApplicationContext()).load(Services.BASE_URL + avaLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViews[3]);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, TransitUser.user.getLogin());
        } catch (JSONException e) {
            Log.d("sendToGetAva: (JSONException)", e.getMessage());
        }
        // endregion
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
            inflater.inflate(TransitUser.user.getLogin().equals(userPage.getLogin()) ? R.menu.user_context : R.menu.other_user_context, menu);
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
            case R.id.saved:
                System.out.println("saved"); // TODO: saved
                return true;
            case R.id.favorites:
                System.out.println("favorites"); // TODO: favorites
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    private void findViews() {
        resources = getResources();
        selfPage = findViewById(R.id.self_page_activity);
        languages = findViewById(R.id.languages);
        imageViews = new ImageView[]{findViewById(R.id.back), findViewById(R.id.change_main_theme), findViewById(R.id.user_context), findViewById(R.id.avatar)};
        textViews = new TextView[]{findViewById(R.id.nick_name), findViewById(R.id.user_name), findViewById(R.id.user_description), findViewById(R.id.amount_posts_title), findViewById(R.id.amount_posts), findViewById(R.id.amount_followings_title), findViewById(R.id.amount_followings), findViewById(R.id.amount_followers_title), findViewById(R.id.amount_followers)};
        imageViewsBottom = new ImageView[]{findViewById(R.id.home), findViewById(R.id.search), findViewById(R.id.add_post), findViewById(R.id.notifications), findViewById(R.id.self_page)};
    }

    private void setListeners() {
        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();

                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        imageViews[0].setOnClickListener(v -> {
            finish();
            SelfPage.userPage = null;
        });

        // initialize menu bottom
        BottomMenu.setListeners(this, new ImageView[]{imageViewsBottom[1], imageViewsBottom[2], imageViewsBottom[3]}, findUser);

        // home
        imageViewsBottom[0].setOnClickListener(v -> {
            finish();
            SelfPage.userPage = null;
        });
        // back
        imageViews[0].setOnClickListener(v -> {
            finish();
            SelfPage.userPage = null;
        });
        // self page
        imageViewsBottom[4].setOnClickListener(v -> {
            (findViewById(R.id.scroll_view)).scrollTo(0, 0);
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