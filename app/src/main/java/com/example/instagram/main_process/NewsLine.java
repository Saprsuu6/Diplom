package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.R;
import com.example.instagram.services.AndroidDownloader;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Errors;
import com.example.instagram.services.FindUser;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPosts;
import com.example.instagram.services.themes_and_backgrounds.Backgrounds;
import com.example.instagram.services.themes_and_backgrounds.Themes;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsLine extends AppCompatActivity {
    private LinearLayout newsLine;
    private Resources resources;
    private ImageView[] imageViewsTop;
    private ImageView[] imageViewsBottom;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PagingViewGetAllPosts pagingView;

    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    public static List<TextView> textViews = new ArrayList<>();
    private FindUser findUser;
    static public Pair<Integer, Post> mapPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_line);
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
        LoadAvatar();

        try {
            pagingView = new PagingViewGetAllPosts(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, this);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    private void setIntents() {
        Intents.setComments(new Intent(this, Comments.class));
        Intents.setSelfPage(new Intent(this, SelfPage.class));
        Intents.setChatList(new Intent(this, ChatList.class));
        Intents.setCreateNewPost(new Intent(this, CreatePost.class));
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
                        Glide.with(getApplicationContext()).load(Services.BASE_URL + avaLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViewsBottom[4]);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, "Andry"); // TODO TransitUser.user.getLogin()
        } catch (JSONException e) {
            Log.d("sendToGetAva: (JSONException)", e.getMessage());
        }
        // endregion
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onResume() {
        super.onResume();

        ThemesBackgrounds.setThemeContent(resources, imageViewsTop[1], getApplicationContext());
        AppCompatDelegate.setDefaultNightMode(ThemesBackgrounds.theme.getValue());
        ThemesBackgrounds.loadBackground(this, newsLine);

        setAnimations();
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
                try {
                    JSONObject postId = new JSONObject();
                    postId.put("postId", NewsLine.mapPost.second.getPostId());

                    Services.sendToDeletePost(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.code() == 200) {
                                pagingView.notifyAdapterToClearByPosition(NewsLine.mapPost.first);
                            }

                            assert response.body() != null;
                            String responseStr = response.body();
                            Errors.deletePost(getApplicationContext(), responseStr);
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("onFailure: (sendToDeletePost)", t.getMessage());
                        }
                    }, postId.toString());
                } catch (JSONException e) {
                    Log.d("JSONException: (sendToDeletePost)", e.getMessage());
                }
                break;
            case R.id.download:
                AndroidDownloader androidDownloader = new AndroidDownloader(this);
                Uri uri = null;

                if (NewsLine.mapPost.second.getResourceImg() != null) {
                    uri = Uri.parse(Services.BASE_URL + NewsLine.mapPost.second.getResourceImg());
                } else if (NewsLine.mapPost.second.getResourceVideo() != null) {
                    uri = Uri.parse(Services.BASE_URL + NewsLine.mapPost.second.getResourceImg());
                }

                if (NewsLine.mapPost.second.getMetadata() != null) {
                    try {
                        JSONObject object = new JSONObject(NewsLine.mapPost.second.getMetadata());
                        androidDownloader.downloadFile(uri, object.getString("Extension"), NewsLine.mapPost.second.getDescription());
                    } catch (JSONException e) {
                        Log.d("JSONException (getMetadata): ", e.getMessage());
                    }
                }
                break;
            case R.id.copy_link_media:
                System.out.println("copy link to media content"); // TODO: copy link to media content
                break;
            case R.id.copy_link:
                System.out.println("copy link to post"); // TODO: copy link to post
                break;
            case R.id.copy_post_qr:
                System.out.println("copy qr link to post"); // TODO: copy qr link to post
                break;
            case R.id.complain_post:
                System.out.println("complain post"); // TODO: complain post
                break;
            default:
                super.onContextItemSelected(item);
                break;
        }
        return true;
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
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
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
        swipeRefreshLayout.setOnRefreshListener(() -> {
            pagingView.notifyAdapterToClearAll();
            swipeRefreshLayout.setRefreshing(false);
        });

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

        // direct
        imageViewsTop[3].setOnClickListener(v -> startActivity(Intents.getChatList()));

        // initialize menu bottom
        BottomMenu.setListeners(this, new ImageView[]{imageViewsBottom[1], imageViewsBottom[2], imageViewsBottom[3]}, findUser);

        // region Bottom menu
        // home
        imageViewsBottom[0].setOnClickListener(v -> (findViewById(R.id.scroll_view)).scrollTo(0, 0));

        // self page
        imageViewsBottom[4].setOnClickListener(v -> startActivity(Intents.getSelfPage()));
        // endregion

        // set system theme on label
        imageViewsTop[0].setOnClickListener(v -> {
            ThemesBackgrounds.theme = Themes.SYSTEM;
            recreate();
        });

        // set night/day theme
        imageViewsTop[1].setOnClickListener(v -> {
            ThemesBackgrounds.theme = ThemesBackgrounds.isNight(resources) ? Themes.DAY : Themes.NIGHT;
            recreate();
        });

        // choose background
        imageViewsTop[2].setOnClickListener(v -> chooseTheme());
    }

    // region Localisation
    private void setStringResources() {
        if (NewsLine.textViews.size() > 0) {
            NewsLine.textViews.get(0).setText(resources.getString(R.string.amount_likes_title));

            try {
                String dateStr = NewsLine.textViews.get(1).getText().toString();
                Date newDate = DateFormatting.formatDate(dateStr);

                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
                NewsLine.textViews.get(1).setText(DateFormatting.formatDate(newDate));
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

            NewsLine.textViews.get(2).setText(resources.getString(R.string.tagged_people));
        }
    }
    // endregion
}