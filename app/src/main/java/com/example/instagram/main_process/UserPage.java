package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DeleteApplicationCache;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.FindUser;
import com.example.instagram.services.Intents;
import com.example.instagram.services.QRGenerator;
import com.example.instagram.services.Resources;
import com.example.instagram.services.Settings;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPostsCells;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;
import com.google.android.flexbox.FlexboxLayout;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

public class UserPage extends AppCompatActivity {
    private class Views {
        public final SwipeRefreshLayout swipeRefreshLayout;
        public final FlexboxLayout flexboxForCellsLayout;
        public final LinearLayout selfPageLayout;
        public final LinearLayout followingsLayout;
        public final LinearLayout followersLayout;
        public final Spinner languagesSpinner;
        public final TextView login;
        public final TextView nick;
        public final TextView surname;
        public final TextView email;
        public final TextView isEmailConfirmed;
        public final TextView description;
        public final TextView birthday;
        public final TextView amountPosts;
        public final TextView followings;
        public final TextView followers;
        public final ImageView back;
        public final ImageView pageContext;
        public final ImageView avatar;
        public final ImageView qr;
        public final ImageView home;
        public final ImageView search;
        public final ImageView addPost;
        public final ImageView notifications;
        public final ImageView selfPage;
        public final Button subscribe;
        public final Button editProfile;
        public final CardView cardViewQr;
        public final ImageView end;
        public Views() {
            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
            flexboxForCellsLayout = findViewById(R.id.layout);
            followingsLayout = findViewById(R.id.followings_layout);
            followersLayout = findViewById(R.id.followers_layout);
            selfPageLayout = findViewById(R.id.self_page_activity);
            languagesSpinner = findViewById(R.id.languages);
            back = findViewById(R.id.back);
            pageContext = findViewById(R.id.user_context);
            avatar = findViewById(R.id.avatar);
            home = findViewById(R.id.home);
            search = findViewById(R.id.search);
            cardViewQr = findViewById(R.id.card_qr);
            addPost = findViewById(R.id.add_post);
            notifications = findViewById(R.id.notifications);
            selfPage = findViewById(R.id.self_page);
            login = findViewById(R.id.user_login);
            nick = findViewById(R.id.user_name);
            surname = findViewById(R.id.user_surname);
            email = findViewById(R.id.user_email);
            isEmailConfirmed = findViewById(R.id.is_email_confirmed);
            description = findViewById(R.id.user_description);
            birthday = findViewById(R.id.user_birthday);
            amountPosts = findViewById(R.id.amount_posts);
            followings = findViewById(R.id.amount_followings);
            followers = findViewById(R.id.amount_followers);
            subscribe = findViewById(R.id.subscribe);
            editProfile = findViewById(R.id.edit_profile);
            qr = findViewById(R.id.qr);
            end = findViewById(R.id.end);
        }
    }

    private PagingAdapterPostsCells pagingView;
    public static User userPage;
    private FindUser findUser;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);

        // region find users
        try {
            findUser = new FindUser(this);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        // endregion

        views = new Views();
        String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
        views.editProfile.setVisibility(login.equals(userPage.getLogin()) ? View.VISIBLE : View.GONE);

        setIntents();
        setListeners();
        registerForContextMenu(views.pageContext);
        UiVisibility.setUiVisibility(this);

        try {
            views.qr.setImageBitmap(QRGenerator.generateQR(UserPage.userPage.getLogin()));
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        Animation mAnimationDrawable = AnimationUtils.loadAnimation(this, R.anim.rotate);
        views.end.startAnimation(mAnimationDrawable);
    }

    @Override
    protected void onResume() {
        super.onResume();

        String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
        if (!UserPage.userPage.getLogin().equals(login)) {
            try {
                JSONObject jsonObject = User.getJSONToKnowIsMeSubscribed(login, UserPage.userPage.getLogin());
                new DoCallBack().setValues(null, this, new Object[]{jsonObject, views.subscribe}).sendToGetIsMeSubscribed();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            LoadAvatar();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        if (views != null) {
            setInfo();
            ThemesBackgrounds.loadBackground(this, views.selfPageLayout);
        }

        try {
            pagingView = new PagingAdapterPostsCells(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, false);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        setStringResources();
    }

    private void setIntents() {
        if (Intents.getEditProfile() == null)
            Intents.setEditProfile(new Intent(this, EditProfile.class));

        if (Intents.getSaved() == null) Intents.setSaved(new Intent(this, Saved.class));
    }

    private void LoadAvatar() throws JSONException {
        new DoCallBack().setValues(null, this, new Object[]{UserPage.userPage.getLogin(), views.avatar, views.selfPage}).sendToGetAvaImage();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getMenuInflater();

        if (v.getId() == R.id.user_context) {
            String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
            inflater.inflate(login.equals(userPage.getLogin()) ? R.menu.user_context : R.menu.other_user_context, menu);
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
                System.out.println("saved");
                startActivity(Intents.getSaved());
                return true;
            case R.id.qr_link:
                views.cardViewQr.setVisibility(View.VISIBLE);
                new Handler().postDelayed(() -> views.cardViewQr.setVisibility(View.GONE), 10000L);
                return true;
            case R.id.log_out:
                DeleteApplicationCache.deleteCache(getApplicationContext());
                Cache.deleteAppSP(this);
                finishAffinity();
                break;
            case R.id.settings:
                try {
                    Settings.getSettingsMenu(UserPage.this).show();
                } catch (WriterException e) {
                    throw new RuntimeException(e);
                }
                return true;
            case R.id.complain:
                System.out.println("complain"); // TODO: complain
                return true;
            default:
                return super.onContextItemSelected(item);
        }

        return true;
    }

    @SuppressLint({"SetTextI18n", "ResourceType"})
    private void setInfo() {
        Resources.setText(UserPage.userPage.getLogin(), views.login);

        if (UserPage.userPage.getNickName().equals(""))
            Resources.setVisibility(View.GONE, views.nick);
        else Resources.setText(UserPage.userPage.getNickName(), views.nick);

        if (UserPage.userPage.getSurname().equals(""))
            Resources.setVisibility(View.GONE, views.surname);
        else Resources.setText(UserPage.userPage.getSurname(), views.surname);

        if (UserPage.userPage.getDescription().equals(""))
            Resources.setVisibility(View.GONE, views.description);
        else Resources.setText(UserPage.userPage.getDescription(), views.description);

        Resources.setText(Integer.toString(UserPage.userPage.getAmountPosts()), views.amountPosts);
        Resources.setText(Integer.toString(UserPage.userPage.getAmountSubscribing()), views.followings);
        Resources.setText(Integer.toString(UserPage.userPage.getAmountSubscribers()), views.followers);
        Resources.setText(UserPage.userPage.getEmail(), views.email);

        Resources.setText(UserPage.userPage.isEmailConfirmed() ? getString(R.string.confirmed) : getString(R.string.not_confirmed), views.isEmailConfirmed);
        Resources.setTextColor(UserPage.userPage.isEmailConfirmed() ? getColor(R.color.success) : getColor(R.color.error), views.isEmailConfirmed);
        Resources.setText(getResources().getString(R.string.birthday_hint) + ": " + DateFormatting.formatDate(UserPage.userPage.getBirthday()), views.birthday);

        if (UserPage.userPage.getLogin().equals(Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString()))) {
            Resources.setVisibility(View.GONE, views.subscribe);
        }
    }

    private void showAgain() throws JSONException {
        if (pagingView != null) {
            pagingView.notifyAdapterToClearAll();
        } else {
            pagingView = new PagingAdapterPostsCells(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), UserPage.this, false);
        }
        views.swipeRefreshLayout.setRefreshing(false);
    }

    private void setListeners() {
        views.swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                showAgain();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        views.followersLayout.setOnClickListener(v -> {
            Cache.saveSP(this, CacheScopes.SELF_PAGE_USER_LOGIN.toString(), UserPage.userPage.getLogin()); // TODO delete after usage

            try {
                findUser.getToFindUser(true).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        views.followingsLayout.setOnClickListener(v -> {
            Cache.saveSP(this, CacheScopes.SELF_PAGE_USER_LOGIN.toString(), UserPage.userPage.getLogin()); // TODO delete after usage

            try {
                findUser.getToFindUser(false).show();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        views.subscribe.setOnClickListener(v -> {
            String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
            boolean isSubscribed = !Cache.loadBoolSP(this, CacheScopes.IS_SUBSCRIBED.toString());
            Cache.saveSP(this, CacheScopes.IS_SUBSCRIBED.toString(), isSubscribed);
            Resources.setText(!isSubscribed ? getResources().getString(R.string.subscribe_btn) : getResources().getString(R.string.unsubscribe_btn), views.subscribe);

            try {
                JSONObject jsonObject = User.getJSONToSubscribe(login, UserPage.userPage.getLogin(), isSubscribed);
                jsonObject.put("token", Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString()));
                new DoCallBack().setValues(null, this, new Object[]{jsonObject}).sendToSetStateSubscribe();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        views.editProfile.setOnClickListener(v -> startActivity(Intents.getEditProfile()));

        // initialize menu bottom
        BottomMenu.setListeners(this, new ImageView[]{views.search, views.addPost, views.notifications}, findUser);

        // home
        views.home.setOnClickListener(v -> {
            finish();
            UserPage.userPage = null;
        });
        // back
        views.back.setOnClickListener(v -> {
            finish();
            UserPage.userPage = null;
        });
        // self page
        views.selfPage.setOnClickListener(v -> (findViewById(R.id.scroll_view)).scrollTo(0, 0));
    }
    
    @SuppressLint("SetTextI18n")
    private void setStringResources() {
        Resources.setText(getResources().getString(R.string.birthday_hint) + ": " + DateFormatting.formatDate(UserPage.userPage.getBirthday()), views.birthday);
        Resources.setText(UserPage.userPage.isEmailConfirmed() ? getString(R.string.confirmed) : getString(R.string.not_confirmed), views.isEmailConfirmed);
    }
}