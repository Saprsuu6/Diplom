package com.example.instagram.main_process;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.R;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.pagination.PaginationCurrentForAllNotifications;
import com.example.instagram.services.pagination.paging_views.PagingAdapterNotifications;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;

public class Notifications extends AppCompatActivity {

    private class Views {
        private final LinearLayout notificationsLayout;
        private final ImageView arrowBack;
        private final SwipeRefreshLayout swipeRefreshLayout;

        public Views() {
            notificationsLayout = findViewById(R.id.notification);
            arrowBack = findViewById(R.id.back);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        }
    }

    private PagingAdapterNotifications pagingView;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        views = new Views();

        UiVisibility.setUiVisibility(this);
        setListeners();

        pagingView = new PagingAdapterNotifications(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemesBackgrounds.loadBackground(this, views.notificationsLayout);
        pagingView = new PagingAdapterNotifications(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this);
    }

    private void sendNewRequest() {
        PagingAdapterNotifications.isEnd = false;
        PaginationCurrentForAllNotifications.resetCurrent();
        pagingView.notifyAdapterToClearAll();
        views.swipeRefreshLayout.setRefreshing(false);
    }

    private void setListeners() {
        views.swipeRefreshLayout.setOnRefreshListener(this::sendNewRequest);

        // back
        views.arrowBack.setOnClickListener(v -> finish());
    }
}