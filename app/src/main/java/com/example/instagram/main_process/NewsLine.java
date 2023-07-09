package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
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
import com.example.instagram.R;
import com.example.instagram.services.AndroidDownloader;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.FindUser;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPosts;
import com.example.instagram.services.themes_and_backgrounds.Themes;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

public class NewsLine extends AppCompatActivity {
    private class Views {
        public final SwipeRefreshLayout swipeRefreshLayout;
        public final Spinner sortBy;
        public final LinearLayout newsLineLayout;
        public final LinearLayout findPostDateLayout;
        public final EditText toFindPostText;
        public final EditText toFindPostFrom;
        public final EditText toFindPostTo;
        public final CheckBox images;
        public final CheckBox videos;
        public final CheckBox audios;
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
            findPostDateLayout = findViewById(R.id.to_find_post_date);
            toFindPostFrom = findViewById(R.id.to_find_post_date_from);
            toFindPostTo = findViewById(R.id.to_find_post_date_to);
            sortBy = findViewById(R.id.sort_by);
            newsLineLayout = findViewById(R.id.news_list);
            toFindPostText = findViewById(R.id.to_find_post_text);
            logo = findViewById(R.id.logo);
            changeMainTheme = findViewById(R.id.change_main_theme);
            changeTheme = findViewById(R.id.change_theme);
            home = findViewById(R.id.home);
            searchUsers = findViewById(R.id.search);
            addNewPost = findViewById(R.id.add_post);
            notifications = findViewById(R.id.notifications);
            selfPage = findViewById(R.id.self_page);
            images = findViewById(R.id.only_image);
            videos = findViewById(R.id.only_video);
            audios = findViewById(R.id.only_audio);
        }
    }

    private PagingAdapterPosts pagingView;
    private FindUser findUser;
    private Views views;
    static public Pair<Integer, Post> mapPost;
    private Calendar dateFrom;
    private Calendar dateTo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_line);

        // region find users
        try {
            findUser = new FindUser(NewsLine.this, NewsLine.this);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        // endregion

        views = new Views();

        views.toFindPostFrom.setInputType(InputType.TYPE_NULL);
        views.toFindPostTo.setInputType(InputType.TYPE_NULL);

        setListeners();
        setIntents();
        UiVisibility.setUiVisibility(NewsLine.this);
    }

    private void setIntents() {
        Intents.setComments(new Intent(NewsLine.this, Comments.class));
        Intents.setSelfPage(new Intent(NewsLine.this, UserPage.class));
        Intents.setCreateNewPost(new Intent(NewsLine.this, CreatePost.class));
        Intents.setNotifications(new Intent(NewsLine.this, Notifications.class));
    }

    private void LoadAvatar() throws JSONException {
        String login = Cache.loadStringSP(NewsLine.this, CacheScopes.USER_LOGIN.toString());
        new DoCallBack().setValues(null, NewsLine.this, new Object[]{login, views.selfPage}).sendToGetAvaImage();
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onResume() {
        super.onResume();

        String ava = Cache.loadStringSP(NewsLine.this, CacheScopes.USER_AVA.toString());

        if (ava.equals("")) {
            try {
                LoadAvatar();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                Glide.with(NewsLine.this).load(ava).diskCacheStrategy(DiskCacheStrategy.ALL).into(views.selfPage);
            } catch (Exception e) {
                Log.d("DoCallBack: ", e.getMessage());
            }
        }

        if (TransitPost.postsToDeleteFromOtherPage.size() > 0) {
            pagingView.notifyAdapterToClearPosts();
            TransitPost.postsToDeleteFromOtherPage.clear();
        }

        if (TransitPost.postsToChangeFromOtherPage.size() > 0) {
            pagingView.notifyAdapterToReplacePosts();
            TransitPost.postsToChangeFromOtherPage.clear();
        }

        AppCompatDelegate.setDefaultNightMode(ThemesBackgrounds.theme.getValue());
        ThemesBackgrounds.setThemeContent(getResources(), views.changeMainTheme, NewsLine.this);
        ThemesBackgrounds.loadBackground(NewsLine.this, views.newsLineLayout);

        DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();

        if (v.getId() == R.id.post_context) {
            String login = Cache.loadStringSP(NewsLine.this, CacheScopes.USER_LOGIN.toString());
            inflater.inflate(login.equals(NewsLine.mapPost.second.getAuthor()) ? R.menu.post_context_menu : R.menu.post_context_menu_hasnt_post, menu);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_post:
                String token = Cache.loadStringSP(NewsLine.this, CacheScopes.USER_TOKEN.toString());

                try {
                    JSONObject jsonObject = Post.getJSONToDeletePost(NewsLine.mapPost.second.getPostId(), token);
                    // delete post
                    new DoCallBack().setValues(() -> pagingView.notifyAdapterToClearByPosition(NewsLine.mapPost.first), NewsLine.this, new Object[]{jsonObject}).deletePost();
                } catch (JSONException e) {
                    Log.d("JSONException: (sendToDeletePost)", e.getMessage());
                }
                break;
            case R.id.download:
                AndroidDownloader androidDownloader = new AndroidDownloader(NewsLine.this);
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
        @SuppressLint("ResourceType") AlertDialog.Builder permissionsDialog = ThemesBackgrounds.getThemeDialog(NewsLine.this, getResources(), NewsLine.this, views.newsLineLayout);
        permissionsDialog.setNegativeButton("Cancel", null);
        permissionsDialog.create().show();
    }

    private void showAgain() throws JSONException {
        JSONObject bodyJSON = getJSONToFind();

        if (pagingView != null) {
            pagingView.notifyAdapterToClearAll();
        }

        pagingView = new PagingAdapterPosts(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), NewsLine.this, NewsLine.this, bodyJSON);
        views.swipeRefreshLayout.setRefreshing(false);
    }

    private JSONObject getJSONToFind() throws JSONException {
        JSONObject bodyJSON;
        JSONObject paramsJSON;

        paramsJSON = new JSONObject();
        switch (views.sortBy.getSelectedItemPosition()) {
            case 0:
                paramsJSON.put("author", views.toFindPostText.getText());
                break;
            case 1:
                paramsJSON.put("description", views.toFindPostText.getText());
                break;
            case 2:
                JSONObject timeInterval = new JSONObject();
                if (dateFrom != null && dateTo != null) {
                    timeInterval.put("from", DateFormatting.formatToDateWithoutTime(dateFrom.getTime()));
                    timeInterval.put("to", DateFormatting.formatToDateWithoutTime(dateTo.getTime()));
                    paramsJSON.put("addDate", timeInterval);
                }
                break;
        }

        if (views.images.isChecked() || views.videos.isChecked() || views.audios.isChecked()) {
            JSONObject onlyMedia = new JSONObject();
            if (views.images.isChecked())
                onlyMedia.put(String.valueOf(onlyMedia.length()), "image");
            if (views.videos.isChecked())
                onlyMedia.put(String.valueOf(onlyMedia.length()), "video");
            if (views.audios.isChecked())
                onlyMedia.put(String.valueOf(onlyMedia.length()), "audio");

            if (onlyMedia.length() > 0) paramsJSON.put("onlyMedia", onlyMedia);
        }

        bodyJSON = new JSONObject();
        bodyJSON.put("params", paramsJSON);

        return bodyJSON;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setListeners() {
        views.images.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                showAgain();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        views.videos.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                showAgain();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        views.audios.setOnCheckedChangeListener((buttonView, isChecked) -> {
            try {
                showAgain();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        views.toFindPostFrom.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewsLine.this, android.R.style.Theme_DeviceDefault_Dialog, (view, year1, month1, dayOfMonth) -> {
                dateFrom = Calendar.getInstance();
                dateFrom.set(year1, month1, dayOfMonth);
                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
                views.toFindPostFrom.setText(DateFormatting.formatDate(dateFrom));

                try {
                    showAgain();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        views.toFindPostTo.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(NewsLine.this, android.R.style.Theme_DeviceDefault_Dialog, (view, year12, month12, dayOfMonth) -> {
                dateTo = Calendar.getInstance();
                dateTo.set(year12, month12, dayOfMonth);
                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
                views.toFindPostTo.setText(DateFormatting.formatDate(dateTo));

                try {
                    showAgain();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        views.sortBy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                    case 1:
                        views.toFindPostText.setVisibility(View.VISIBLE);
                        views.findPostDateLayout.setVisibility(View.GONE);
                        break;
                    case 2:
                        views.findPostDateLayout.setVisibility(View.VISIBLE);
                        views.toFindPostText.setVisibility(View.GONE);
                        break;

                }

                try {
                    showAgain();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("onNothingSelected");
            }
        });

        views.toFindPostText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    JSONObject bodyJSON = getJSONToFind();
                    pagingView = new PagingAdapterPosts(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), NewsLine.this, NewsLine.this, bodyJSON);
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

        // initialize menu bottom
        BottomMenu.setListeners(NewsLine.this, new ImageView[]{views.searchUsers, views.addNewPost, views.notifications}, findUser);

        // region Bottom menu
        // home
        views.home.setOnClickListener(v -> (findViewById(R.id.scroll_view)).scrollTo(0, 0));
        // self page
        views.selfPage.setOnClickListener(v -> {
            String login = Cache.loadStringSP(NewsLine.this, CacheScopes.USER_LOGIN.toString());

            try {
                new DoCallBack().setValues(() -> startActivity(Intents.getSelfPage()), NewsLine.this, new Object[]{login}).sendToGetCurrentUser();
            } catch (JSONException e) {
                throw new RuntimeException(e);
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
            ThemesBackgrounds.theme = ThemesBackgrounds.isNight(getResources()) ? Themes.DAY : Themes.NIGHT;
            recreate();
        });

        // choose background
        views.changeTheme.setOnClickListener(v -> chooseTheme());
    }
}