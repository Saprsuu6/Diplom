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
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPosts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagingViewGetAllPosts extends PagingView {
    private PaginationAdapterPosts paginationAdapter;
    private final JSONObject body;
    private boolean isStart = true;
    public static boolean isEnd = false;

    public PagingViewGetAllPosts(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity, JSONObject body) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);
        this.body = body;
        isEnd = false;
        PaginationCurrentForAllPosts.resetCurrent();

        body.put("from", Integer.toString(PaginationCurrentForAllPosts.current));
        body.put("amount", Integer.toString(PaginationCurrentForAllPosts.amountOfPagination));

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
    public void notifyAdapterToReplacePosts() {
        for (Post post : TransitPost.postsToChangeFromOtherPage) {
            for (int i = 0; i < paginationAdapter.getPostsLibrary().getDataArrayList().size(); i++) {
                Post postToReplace = paginationAdapter.getPostsLibrary().getDataArrayList().get(i);

                if (postToReplace.getPostId().equals(post.getPostId())) {
                    paginationAdapter.getPostsLibrary().getDataArrayList().removeIf(postDuplicate -> postDuplicate.getPostId().equals(post.getPostId()));
                    paginationAdapter.getPostsLibrary().getDataArrayList().add(i, post.clone(post));
                }
            }
        }
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearPosts() {
        paginationAdapter.getPostsLibrary().getDataArrayList().removeIf(postDuplicate -> TransitPost.postsToDeleteFromOtherPage.stream().anyMatch(item -> item.getPostId().equals(postDuplicate.getPostId())));
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearByPosition(int position) {
        Post post = paginationAdapter.getPostsLibrary().getDataArrayList().get(position);
        paginationAdapter.getPostsLibrary().getDataArrayList().removeIf(postDuplicate -> postDuplicate.getPostId().equals(post.getPostId()));
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAllLibrary() {
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearAdapter() {
        paginationAdapter.getPostsLibrary().getDataArrayList().clear();
        paginationAdapter.notifyDataSetChanged();

        PagingViewGetAllPosts.isEnd = false;
        PaginationCurrentForAllPosts.resetCurrent();
    }

    public void notifyAdapterToClearAll() {
        clearAdapter();

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
        if (!PagingViewGetAllPosts.isEnd) {
            startSkeletonAnim();

            // if it start of find
            if (!isStart) {
                body.put("from", Integer.toString(PaginationCurrentForAllPosts.current));
                body.put("amount", Integer.toString(PaginationCurrentForAllPosts.amountOfPagination));
            }

            isStart = false;

            Services.sendToFindPost(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllPosts.amountOfPagination) {
                                    PagingViewGetAllPosts.isEnd = true;
                                }

                                isBusy = false;
                                PaginationCurrentForAllPosts.nextCurrent();
                                postsLibrary.setDataArrayList(jsonArray);
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
