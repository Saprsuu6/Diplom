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
import com.example.instagram.services.TransitComment;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.PaginationCurrentForAllPosts;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPosts;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Comment;

import java.util.Objects;
import java.util.stream.Stream;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagingViewGetAllPosts extends PagingView {
    private PaginationAdapterPosts paginationAdapter;
    public static boolean isEnd = false;

    public PagingViewGetAllPosts(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity) {
        super(scrollView, recyclerView, shimmerLayout, context, activity);
        isEnd = false;
        PaginationCurrentForAllPosts.resetCurrent();

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
                    paginationAdapter.getPostsLibrary().getDataArrayList().remove(i);
                    paginationAdapter.getPostsLibrary().getDataArrayList().add(i, post.clone(post));
                }
            }
        }
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearPosts() {
        // TODO debug logic
        paginationAdapter.getPostsLibrary().getDataArrayList().removeIf(postDuplicate -> TransitPost.postsToDeleteFromOtherPage.stream().anyMatch(item -> item.getPostId().equals(postDuplicate.getPostId())));
        paginationAdapter.notifyDataSetChanged();
    }

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

        PagingViewGetAllPosts.isEnd = false;
        PaginationCurrentForAllPosts.resetCurrent();

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

            Services.sendToGetAllPosts(new Callback<>() {
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
            }, PaginationCurrentForAllPosts.current, PaginationCurrentForAllPosts.amountOfPagination);
        }
    }
}
