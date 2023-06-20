package com.example.instagram.services.pagination.paging_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.Post;
import com.example.instagram.services.Services;
import com.example.instagram.services.SharedPreferences;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPosts;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Iterator;
import java.util.Objects;

import io.supercharge.shimmerlayout.ShimmerLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagingViewGetAllPosts extends PagingView {
    private PaginationAdapterPosts paginationAdapter;
    private static final int paginationAmount = 3;

    public PagingViewGetAllPosts(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);

        // initialise adapter
        paginationAdapter = new PaginationAdapterPosts(activity, context, postsLibrary);

        // set layout manager
        LinearLayoutManager manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        try {
            getData();
        } catch (JSONException e) {
            Log.d("sendToGetAllPosts: ", e.getMessage());
        }
    }

    // region notifiers
    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearByPosition(int position) {
        Post post = paginationAdapter.getPostsLibrary().getDataArrayList().get(position);
        paginationAdapter.getPostsLibrary().getDataArrayList().removeIf(postDuplicate -> Objects.equals(postDuplicate.getPostId(), post.getPostId()));
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAllLibrary() {
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearAll() {
        paginationAdapter.getPostsLibrary().getDataArrayList().clear();
        paginationAdapter.notifyDataSetChanged();

        try {
            getData();
        } catch (JSONException e) {
            Log.d("sendToGetAllPosts: ", e.getMessage());
        }
    }
    // endregion

    @Override
    protected void setPaginationAdapter() {
        paginationAdapter = new PaginationAdapterPosts(activity, context, postsLibrary);
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
        }, PagingViewGetAllPosts.paginationAmount, null);
    }
}
