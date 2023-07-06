package com.example.instagram.main_process;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.pagination.paging_views.PagingAdapterComments;
import com.example.instagram.services.pagination.paging_views.PagingAdapterNotifications;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import java.util.Locale;

public class Notifications extends AppCompatActivity {

    private class Views {
        private final LinearLayout notificationsLayout;
        private final ImageView arrowBack;
        private final TextView title;
        private final Spinner languagesSpinner;
        private final SwipeRefreshLayout swipeRefreshLayout;

        public Views() {
            notificationsLayout = findViewById(R.id.notification);
            arrowBack = findViewById(R.id.back);
            title = findViewById(R.id.notifications_title);
            languagesSpinner = findViewById(R.id.languages);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        }
    }

    private PagingAdapterNotifications pagingView;
    private Resources resources;
    private Localisation localisation;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        views = new Views();
        resources = getResources();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        UiVisibility.setUiVisibility(this);
        setListeners();

        pagingView = new PagingAdapterNotifications(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
        ThemesBackgrounds.loadBackground(this, views.notificationsLayout);
    }

    private void sendNewRequest() {
        // PagingAdapterComments.isEnd = false;
        //PaginationCurrentForAllComments.resetCurrent();
        //pagingView.notifyAdapterToClearAll();
        views.swipeRefreshLayout.setRefreshing(false);
    }

    private void setListeners() {
        views.swipeRefreshLayout.setOnRefreshListener(this::sendNewRequest);

        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                //setStringResources();

                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

        // back
        views.arrowBack.setOnClickListener(v -> finish());
    }
}