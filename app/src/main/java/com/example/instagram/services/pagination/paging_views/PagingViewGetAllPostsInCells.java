package com.example.instagram.services.pagination.paging_views;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.services.Services;
import com.example.instagram.services.SharedPreferences;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPosts;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPostsCells;

import org.json.JSONArray;
import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagingViewGetAllPostsInCells extends PagingView {
    private PaginationAdapterPostsCells paginationAdapter;
    private int lastPosition;
    private final LinearLayoutManager manager;

    public PagingViewGetAllPostsInCells(NestedScrollView scrollView, RecyclerView recyclerView,
                                        ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity,
                                        int page, int onePageLimit) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity, page, onePageLimit);

        // initialise adapter
        paginationAdapter = new PaginationAdapterPostsCells(context, activity, postsLibrary);

        // set layout manager
        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        loadPosition();
        setListeners();

        try {
            getData(page, onePageLimit);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
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
        paginationAdapter = new PaginationAdapterPostsCells(context, activity, postsLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData(int page, int onePageLimit) throws JSONException {
        startSkeletonAnim();

        Services.sendToGetPosts(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stopSkeletonAnim();

                    // add new data
                    String body = response.body();
                    try {
                        postsLibrary.setDataArrayList(new JSONArray(body));

                        setPaginationAdapter();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, page, onePageLimit);
    }
}
