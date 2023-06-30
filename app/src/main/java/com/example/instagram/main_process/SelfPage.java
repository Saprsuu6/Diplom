package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.services.Animation;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DeleteApplicationCache;
import com.example.instagram.services.FindUser;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPostsInCells;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;

import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SelfPage extends AppCompatActivity {
    private class Views {
        public final FlexboxLayout flexboxForCellsLayout;
        public final LinearLayout selfPageLayout;
        public final Spinner languagesSpinner;
        public final TextView login;
        public final TextView nick;
        public final TextView surname;
        public final TextView email;
        public final TextView description;
        public final TextView birthday;
        public final TextView amountPosts;
        public final TextView followings;
        public final TextView followers;
        public final ImageView back;
        public final ImageView pageContext;
        public final ImageView avatar;
        public final ImageView home;
        public final ImageView search;
        public final ImageView addPost;
        public final ImageView notifications;
        public final ImageView selfPage;
        public final Button subscribe;
        public final Button editProfile;

        public Views() {
            flexboxForCellsLayout = findViewById(R.id.layout);
            selfPageLayout = findViewById(R.id.self_page_activity);
            languagesSpinner = findViewById(R.id.languages);
            back = findViewById(R.id.back);
            pageContext = findViewById(R.id.user_context);
            avatar = findViewById(R.id.avatar);
            home = findViewById(R.id.home);
            search = findViewById(R.id.search);
            addPost = findViewById(R.id.add_post);
            notifications = findViewById(R.id.notifications);
            selfPage = findViewById(R.id.self_page);
            login = findViewById(R.id.user_login);
            nick = findViewById(R.id.user_name);
            surname = findViewById(R.id.user_surname);
            email = findViewById(R.id.user_email);
            description = findViewById(R.id.user_description);
            birthday = findViewById(R.id.user_birthday);
            amountPosts = findViewById(R.id.amount_posts);
            followings = findViewById(R.id.amount_followings);
            followers = findViewById(R.id.amount_followers);
            subscribe = findViewById(R.id.subscribe);
            editProfile = findViewById(R.id.edit_profile);
        }
    }

    public static User userPage;
    private Resources resources;
    private Localisation localisation;
    private FindUser findUser;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_self_page);

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
        views.editProfile.setVisibility(TransitUser.user.getLogin().equals(userPage.getLogin()) ? View.VISIBLE : View.GONE);

        setIntents();
        setListeners();
        registerForContextMenu(views.pageContext);
        LoadAvatar();
        UiVisibility.setUiVisibility(this);
        Animation.getAnimations(views.selfPageLayout).start();

        try {
            new PagingViewGetAllPostsInCells(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, this);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (views != null) {
            setInfo();
            Localisation.setFirstLocale(views.languagesSpinner);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void setIntents() {
        if (Intents.getEditProfile() == null)
            Intents.setEditProfile(new Intent(this, EditProfile.class));
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
                        Glide.with(getApplicationContext()).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(views.avatar);
                        Glide.with(getApplicationContext()).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(views.selfPage);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, SelfPage.userPage.getLogin());
        } catch (JSONException e) {
            Log.d("sendToGetAva: (JSONException)", e.getMessage());
        }
        // endregion
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
            case R.id.statistic:
                System.out.println("statistic"); // TODO: statistic
                return true;
            case R.id.saved:
                System.out.println("saved"); // TODO: saved
                return true;
            case R.id.favorites:
                System.out.println("favorites"); // TODO: favorites
                return true;
            case R.id.link:
                System.out.println("link"); // TODO: favorites
                return true;
            case R.id.qr_link:
                System.out.println("qr_link"); // TODO: favorites
                return true;
            case R.id.log_out:
                DeleteApplicationCache.deleteCache(getApplicationContext());
                finishAffinity();
                break;
            case R.id.complain:
                System.out.println("complain"); // TODO: favorites
                return true;
            default:
                return super.onContextItemSelected(item);
        }

        return true;
    }

    @SuppressLint("SetTextI18n")
    private void setInfo() {
        views.login.setText(SelfPage.userPage.getLogin());

        if (SelfPage.userPage.getNickName().equals("")) views.nick.setVisibility(View.GONE);
        else views.nick.setText(SelfPage.userPage.getNickName());

        if (SelfPage.userPage.getSurname().equals("")) views.surname.setVisibility(View.GONE);
        else views.surname.setText(SelfPage.userPage.getSurname());

        if (SelfPage.userPage.getDescription().equals(""))
            views.description.setVisibility(View.GONE);
        else views.description.setText(SelfPage.userPage.getDescription());

        views.amountPosts.setText(Integer.toString(SelfPage.userPage.getAmountPosts()));
        views.followings.setText("101");
        views.followers.setText("102");
        views.email.setText(SelfPage.userPage.getEmail());
        views.birthday.setText(resources.getString(R.string.birthday_hint) + ": " + DateFormatting.formatDate(SelfPage.userPage.getBirthday()));
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
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

        views.subscribe.setOnClickListener(v -> {
            // TODO realize subscribe
        });

        views.editProfile.setOnClickListener(v -> startActivity(Intents.getEditProfile()));

        // initialize menu bottom
        BottomMenu.setListeners(this, new ImageView[]{views.search, views.addPost, views.notifications}, findUser);

        // home
        views.home.setOnClickListener(v -> {
            finish();
            SelfPage.userPage = null;
        });
        // back
        views.back.setOnClickListener(v -> {
            finish();
            SelfPage.userPage = null;
        });
        // self page
        views.selfPage.setOnClickListener(v -> (findViewById(R.id.scroll_view)).scrollTo(0, 0));
    }

    // region Localisation
    @SuppressLint("SetTextI18n")
    private void setStringResources() {
        views.amountPosts.setText(resources.getString(R.string.posts));
        views.followings.setText(resources.getString(R.string.followings));
        views.followers.setText(resources.getString(R.string.followers));
        views.birthday.setText(resources.getString(R.string.birthday_hint) + ": " + DateFormatting.formatDate(SelfPage.userPage.getBirthday()));
    }
    // endregion
}