package com.example.instagram.services.pagination.paging_views;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.services.Services;
import com.example.instagram.services.SharedPreferences;
import com.example.instagram.services.pagination.PagingView;
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
    private static final int paginationAmount = 3;
    private final LinearLayoutManager manager;

    public PagingViewGetAllPostsInCells(NestedScrollView scrollView, RecyclerView recyclerView,
                                        ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);

        // initialise adapter
        assert activity != null;
        paginationAdapter = new PaginationAdapterPostsCells(context, activity, postsLibrary);

        // set layout manager
        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        loadPosition();
        setListeners();

        try {
            setToBegin();
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadPosition() {
        int position = SharedPreferences.loadIntSP(context, "lastNewsPositionSelfPage");
        recyclerView.scrollToPosition(position);
    }

    public void savePosition() {
        if (SharedPreferences.loadIntSP(context, "lastNewsPositionSelfPage") == 0)
            SharedPreferences.saveSP(context, "lastNewsPositionSelfPage", lastPosition);
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
        assert activity != null;
        paginationAdapter = new PaginationAdapterPostsCells(context, activity, postsLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData() throws JSONException {
        startSkeletonAnim();

        Services.sendToGetAllPosts(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body();

                    if (!body.equals("[]")) {
                        try {
                            postsLibrary.setDataArrayList(new JSONArray(body));
                            setPaginationAdapter();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    stopSkeletonAnim();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("sendToGetAllPosts: ", t.getMessage());
            }
        }, PagingViewGetAllPostsInCells.paginationAmount, null);
    }

    public void setToBegin()throws JSONException {
        startSkeletonAnim();

        Services.sendToGetAllPosts(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        getData();
                    } catch (JSONException e) {
                        Log.d("sendToGetAllPosts: ", e.getMessage());
                    }

                    stopSkeletonAnim();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("sendToGetAllPosts: ", t.getMessage());
            }
        }, PagingViewGetAllPostsInCells.paginationAmount, "begin");
    }
}
