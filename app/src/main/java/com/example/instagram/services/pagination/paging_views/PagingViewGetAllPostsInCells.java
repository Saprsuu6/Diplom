package com.example.instagram.services.pagination.paging_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.main_process.SelfPage;
import com.example.instagram.services.Services;
import com.example.instagram.services.SharedPreferences;
import com.example.instagram.services.TransitComment;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.pagination.PaginationCurrentForAllPosts;
import com.example.instagram.services.pagination.PaginationCurrentForAllPostsInCells;
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
    private final LinearLayoutManager manager;
    public static boolean isEnd = false;

    public PagingViewGetAllPostsInCells(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);
        isEnd = false;
        PaginationCurrentForAllPostsInCells.resetCurrent();

        // initialise adapter
        paginationAdapter = new PaginationAdapterPostsCells(context, activity, postsLibrary, this);

        // set layout manager
        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        try {
            getData();
        } catch (JSONException e) {
            Log.d("JSONException: ", e.getMessage());
        }
    }

    // region notifiers
    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearAll() {
        paginationAdapter.getPostsLibrary().getDataArrayList().clear();
        paginationAdapter.notifyDataSetChanged();

        PagingViewGetAllPostsInCells.isEnd = false;
        PaginationCurrentForAllPostsInCells.resetCurrent();

        try {
            getData();
        } catch (JSONException e) {
            Log.d("sendToGetAllPosts: ", e.getMessage());
        }
    }
    // endregion

    @Override
    protected void setPaginationAdapter() {
        assert activity != null;
        paginationAdapter = new PaginationAdapterPostsCells(context, activity, postsLibrary, this);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData() throws JSONException {
        if (!PagingViewGetAllPostsInCells.isEnd) {
            startSkeletonAnim();

            Services.sendToGetPostsOfUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllPostsInCells.amountOfPagination) {
                                    PagingViewGetAllPostsInCells.isEnd = true;
                                }

                                isBusy = false;
                                PaginationCurrentForAllPostsInCells.nextCurrent();
                                postsLibrary.setDataArrayList(jsonArray);
                                setPaginationAdapter();
                            } catch (JSONException e) {
                                Log.d("JSONException: ", e.getMessage());
                            }
                        }

                        stopSkeletonAnim();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetPostsOfUser: ", t.getMessage());
                }
            }, PaginationCurrentForAllPostsInCells.current, PaginationCurrentForAllPostsInCells.amountOfPagination, SelfPage.userPage.getLogin());
        }
    }
}
