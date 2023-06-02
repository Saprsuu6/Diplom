package com.example.instagram.services.pagination.paging_views;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.services.Services;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterUsers;

import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagingViewFindUsers extends PagingView {
    private PaginationAdapterUsers paginationAdapter;

    public PagingViewFindUsers(NestedScrollView scrollView, RecyclerView recyclerView,
                               ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity,
                               int page, int onePageLimit) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity,  page, onePageLimit);

        // initialise adapter
        paginationAdapter = new PaginationAdapterUsers(context, postsLibrary);

        // set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        try {
            getData(page, onePageLimit);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    protected void setPaginationAdapter() {
        paginationAdapter = new PaginationAdapterUsers(context, postsLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData(int page, int onePageLimit) throws JSONException {
        Services.sendToGetUsers(new Callback<String>() {
            @Override
            public void onResponse(@Nullable Call<String> call, @Nullable Response<String> response) {
                System.out.println("getUsers");
            }

            @Override
            public void onFailure(@Nullable Call<String> call, @Nullable Throwable t) {
                assert t != null;
                System.out.println(t.getMessage());
            }
        }, 1, 20);
    }
}
