package com.example.instagram.main_process;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.R;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPostsCells;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;

public class Saved extends AppCompatActivity {

    private class Views {
        private final LinearLayout savedLayout;
        private final ImageView arrowBack;
        private final SwipeRefreshLayout swipeRefreshLayout;

        public Views() {
            savedLayout = findViewById(R.id.notification);
            arrowBack = findViewById(R.id.back);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        }
    }


    private PagingAdapterPostsCells pagingView;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved);

        views = new Views();

        UiVisibility.setUiVisibility(this);
        setListeners();

        try {
            pagingView = new PagingAdapterPostsCells(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, true);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemesBackgrounds.loadBackground(this, views.savedLayout);
    }

    private void showAgain() throws JSONException {
        if (pagingView != null) {
            pagingView.notifyAdapterToClearAll();
        } else {
            pagingView = new PagingAdapterPostsCells(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this,true);
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

        // back
        views.arrowBack.setOnClickListener(v -> finish());
    }
}