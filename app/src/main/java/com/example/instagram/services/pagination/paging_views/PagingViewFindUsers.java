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

import com.example.instagram.DAOs.Post;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.pagination.PaginationCurrentForAllPosts;
import com.example.instagram.services.pagination.PaginationCurrentForAllUsers;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterUsers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagingViewFindUsers extends PagingView {
    private final JSONObject body;
    private PaginationAdapterUsers paginationAdapter;
    public static boolean isEnd = false;
    private boolean isStart = true;

    public PagingViewFindUsers(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity, JSONObject body) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);
        this.body = body;
        isEnd = false;
        PaginationCurrentForAllUsers.resetCurrent();

        body.put("from", Integer.toString(PaginationCurrentForAllUsers.current));
        body.put("amount", Integer.toString(PaginationCurrentForAllUsers.amountOfPagination));

        // initialise adapter
        paginationAdapter = new PaginationAdapterUsers(context, usersLibrary);

        // set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        try {
            getData();
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void setPaginationAdapter() {
        paginationAdapter = new PaginationAdapterUsers(context, usersLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }

    // region notifiers
    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearAll() {
        paginationAdapter.getUsersLibrary().getDataArrayList().clear();
        paginationAdapter.notifyDataSetChanged();

        PagingViewFindUsers.isEnd = false;
        PaginationCurrentForAllUsers.resetCurrent();

        try {
            getData();
        } catch (JSONException e) {
            Log.d("sendToGetAllPosts: ", e.getMessage());
        }
    }
    // endregion

    @Override
    protected void getData() throws JSONException {
        if (!PagingViewFindUsers.isEnd) {
            startSkeletonAnim();

            // if it start of find
            if (!isStart) {
                body.put("from", Integer.toString(PaginationCurrentForAllUsers.current));
                body.put("amount", Integer.toString(PaginationCurrentForAllUsers.amountOfPagination));
            }

            isStart = false;

            Services.sendToFindUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllUsers.amountOfPagination) {
                                    PagingViewFindUsers.isEnd = true;
                                }

                                isBusy = false;
                                PaginationCurrentForAllUsers.nextCurrent();
                                usersLibrary.setDataArrayList(jsonArray);
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
            }, body.toString());
        }
    }
}
