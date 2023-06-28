package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.services.AndroidDownloader;
import com.example.instagram.services.Animation;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Errors;
import com.example.instagram.services.FindUser;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.pagination.paging_views.PagingViewFindUsers;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPosts;
import com.example.instagram.services.themes_and_backgrounds.Themes;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsLine extends AppCompatActivity {
    private class Views {
        public final SwipeRefreshLayout swipeRefreshLayout;
        public final LinearLayout newsLineLayout;
        public final Spinner languagesSpinner;
        public final EditText toFindPost;
        public final ImageView logo;
        public final ImageView changeMainTheme;
        public final ImageView changeTheme;
        public final ImageView home;
        public final ImageView searchUsers;
        public final ImageView addNewPost;
        public final ImageView notifications;
        public final ImageView selfPage;

        public Views() {
            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
            newsLineLayout = findViewById(R.id.news_list);
            toFindPost = findViewById(R.id.toFindPost);
            languagesSpinner = findViewById(R.id.languages);
            logo = findViewById(R.id.logo);
            changeMainTheme = findViewById(R.id.change_main_theme);
            changeTheme = findViewById(R.id.change_theme);
            home = findViewById(R.id.home);
            searchUsers = findViewById(R.id.search);
            addNewPost = findViewById(R.id.add_post);
            notifications = findViewById(R.id.notifications);
            selfPage = findViewById(R.id.self_page);
        }
    }

    private Resources resources;
    private PagingViewGetAllPosts pagingView;
    private Localisation localisation;
    private FindUser findUser;
    private Views views;
    static public Pair<Integer, Post> mapPost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_line);

        // region find users
        try {
            findUser = new FindUser(this, this);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        // endregion

        resources = getResources();
        views = new Views();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        setListeners();
        LoadAvatar();
        setIntents();
        UiVisibility.setUiVisibility(this);
        Animation.getAnimations(views.newsLineLayout).start();

        try {
            showAgain();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void setIntents() {
        Intents.setComments(new Intent(this, Comments.class));
        Intents.setSelfPage(new Intent(this, SelfPage.class));
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
                        String imagePath = Services.BASE_URL + getString(R.string.root_folder) + avaLink;
                        Glide.with(getApplicationContext()).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(views.selfPage);
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

    @SuppressLint("WrongConstant")
    @Override
    protected void onResume() {
        super.onResume();

        Localisation.setFirstLocale(views.languagesSpinner);

        if (TransitPost.postsToDeleteFromOtherPage.size() > 0) {
            pagingView.notifyAdapterToClearPosts();
            TransitPost.postsToDeleteFromOtherPage.clear();
        }

        if (TransitPost.postsToChangeFromOtherPage.size() > 0) {
            pagingView.notifyAdapterToReplacePosts();
            TransitPost.postsToChangeFromOtherPage.clear();
        }

        AppCompatDelegate.setDefaultNightMode(ThemesBackgrounds.theme.getValue());
        ThemesBackgrounds.setThemeContent(resources, views.changeMainTheme, getApplicationContext());
        ThemesBackgrounds.loadBackground(this, views.newsLineLayout);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        if (v.getId() == R.id.post_context) {
            inflater.inflate(TransitUser.user.getLogin().equals(NewsLine.mapPost.second.getAuthor()) ? R.menu.post_context_menu : R.menu.post_context_menu_hasnt_post, menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_post:
                JSONObject body = new JSONObject();

                try {
                    body.put("postId", NewsLine.mapPost.second.getPostId());
                    body.put("token", TransitUser.user.getToken());

                    Services.sendToDeletePost(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                String responseStr = response.body();
                                Errors.delete(getApplicationContext(), responseStr).show();

                                pagingView.notifyAdapterToClearByPosition(NewsLine.mapPost.first);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("onFailure: (sendToDeletePost)", t.getMessage());
                        }
                    }, body.toString());
                } catch (JSONException e) {
                    Log.d("JSONException: (sendToDeletePost)", e.getMessage());
                }
                break;
            case R.id.download:
                AndroidDownloader androidDownloader = new AndroidDownloader(this);
                Uri uri = Uri.parse(Services.BASE_URL + NewsLine.mapPost.second.getResourceMedia());

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

    private void chooseTheme() {
        @SuppressLint("ResourceType") AlertDialog.Builder permissionsDialog = ThemesBackgrounds.getThemeDialog(this, resources, this, views.newsLineLayout);
        permissionsDialog.setNegativeButton("Cancel", null);
        permissionsDialog.create().show();
    }

    private void showAgain() throws JSONException {
        if (pagingView != null) {
            pagingView.notifyAdapterToClearAll();
        } else {
            JSONObject bodyJSON = getJSONToFind();
            pagingView = new PagingViewGetAllPosts(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, this, bodyJSON);
        }
        views.swipeRefreshLayout.setRefreshing(false);
    }

    private JSONObject getJSONToFind() throws JSONException {
        JSONObject bodyJSON;
        JSONObject paramsJSON;

        paramsJSON = new JSONObject();
        paramsJSON.put("author", views.toFindPost.getText());
        // TODO or
        //paramsJSON.put("description", views.toFindPost.getText());

        bodyJSON = new JSONObject();
        bodyJSON.put("params", paramsJSON);

        return bodyJSON;
    }

    private void setListeners() {
        Activity thisActivity = this;
        views.toFindPost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    JSONObject bodyJSON = getJSONToFind();
                    pagingView = new PagingViewGetAllPosts(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), getApplicationContext(), thisActivity, bodyJSON);
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            }
        });

        views.swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                showAgain();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
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
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

        // initialize menu bottom
        BottomMenu.setListeners(this, new ImageView[]{views.searchUsers, views.addNewPost, views.notifications}, findUser);

        // region Bottom menu
        // home
        views.home.setOnClickListener(v -> (findViewById(R.id.scroll_view)).scrollTo(0, 0));
        // self page
        views.selfPage.setOnClickListener(v -> {
            try {
                Services.sendToGetCurrentUser(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject user;

                            try {
                                user = new JSONObject(response.body());

                                SelfPage.userPage = User.getPublicUser(user, TransitUser.user.getLogin());
                                startActivity(Intents.getSelfPage());
                            } catch (JSONException | ParseException e) {
                                Log.d("JSONException", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToGetCurrentUser: (onFailure)", t.getMessage());
                    }
                }, TransitUser.user.getLogin());
            } catch (JSONException e) {
                Log.d("JSONException: ", e.getMessage());
            }
        });
        // endregion

        // set system theme on label
        views.logo.setOnClickListener(v -> {
            ThemesBackgrounds.theme = Themes.SYSTEM;
            recreate();
        });

        // set night/day theme
        views.changeMainTheme.setOnClickListener(v -> {
            ThemesBackgrounds.theme = ThemesBackgrounds.isNight(resources) ? Themes.DAY : Themes.NIGHT;
            recreate();
        });

        // choose background
        views.changeTheme.setOnClickListener(v -> chooseTheme());
    }

    private void setStringResources() {
        views.toFindPost.setHint(getString(R.string.find_post));
        pagingView.notifyAllLibrary();
    }
}