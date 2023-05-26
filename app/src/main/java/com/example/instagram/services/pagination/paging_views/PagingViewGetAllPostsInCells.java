package com.example.instagram.services.pagination.paging_views;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.services.SharedPreferences;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPosts;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPostsCells;

import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingViewGetAllPostsInCells extends PagingView {
    private PaginationAdapterPostsCells paginationAdapter;
    private int lastPosition;
    private final LinearLayoutManager manager;

    public PagingViewGetAllPostsInCells(NestedScrollView scrollView, RecyclerView recyclerView,
                                 ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity,
                                 int page, int onePageLimit) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity, page, onePageLimit);

        // initialise adapter
        paginationAdapter = new PaginationAdapterPostsCells(context, activity, mainDataLibrary);

        // set layout manager
        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        loadPosition();
        setListeners();

        super.getData(page, onePageLimit);
    }

    public void loadPosition() {
        int position = SharedPreferences.loadIntSP(context, "lastNewsPosition");
        recyclerView.scrollToPosition(position);
    }

    public void savePosition() {
        if (SharedPreferences.loadIntSP(context, "lastNewsPosition") == 0)
            SharedPreferences.saveSP(context, "lastNewsPosition", lastPosition);
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
        paginationAdapter = new PaginationAdapterPostsCells(context, activity, mainDataLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }
}
