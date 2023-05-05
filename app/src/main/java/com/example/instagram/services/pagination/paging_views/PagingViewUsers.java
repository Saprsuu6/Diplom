package com.example.instagram.services.pagination.paging_views;

import android.app.Activity;
import android.graphics.Point;
import android.view.Display;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterUsers;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingViewUsers extends PagingView {
    private PaginationAdapterUsers paginationAdapter;

    public PagingViewUsers(NestedScrollView scrollView, RecyclerView recyclerView,
                           ShimmerLayout shimmerLayout, Activity activity,
                           int page, int onePageLimit) {
        super(scrollView, recyclerView, shimmerLayout, activity, page, onePageLimit);

        // initialise adapter
        paginationAdapter = new PaginationAdapterUsers(activity, mainDataLibrary);

        // set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        super.getData(page, onePageLimit);
    }

    @Override
    protected void setPaginationAdapter() {
        paginationAdapter = new PaginationAdapterUsers(activity, mainDataLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }
}
