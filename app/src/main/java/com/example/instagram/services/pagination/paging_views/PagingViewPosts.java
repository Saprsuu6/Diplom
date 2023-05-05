package com.example.instagram.services.pagination.paging_views;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.services.SharedPreferences;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPosts;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingViewPosts extends PagingView {
    private PaginationAdapterPosts paginationAdapter;
    private int lastPosition;
    private LinearLayoutManager manager;

    public PagingViewPosts(NestedScrollView scrollView, RecyclerView recyclerView,
                           ShimmerLayout shimmerLayout, Activity activity,
                           int page, int onePageLimit) {
        super(scrollView, recyclerView, shimmerLayout, activity, page, onePageLimit);

        // initialise adapter
        paginationAdapter = new PaginationAdapterPosts(activity, mainDataLibrary);

        // set layout manager
        manager = new LinearLayoutManager(activity);
        recyclerView.setLayoutManager(manager);

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        loadPosition();
        setListeners();

        super.getData(page, onePageLimit);
    }

    public void loadPosition() {
        int position = SharedPreferences.loadIntSP(activity, "lastNewsPosition");
        recyclerView.scrollToPosition(position);
    }

    public void savePosition() {
        if (SharedPreferences.loadIntSP(activity, "lastNewsPosition") == 0)
            SharedPreferences.saveSP(activity, "lastNewsPosition", lastPosition);
    }

    private void setListeners() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = manager.findLastVisibleItemPosition();
            }
        });
    }

    @Override
    protected void setPaginationAdapter() {
        paginationAdapter = new PaginationAdapterPosts(activity, mainDataLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }
}
