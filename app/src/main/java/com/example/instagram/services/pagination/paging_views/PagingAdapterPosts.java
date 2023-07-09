package com.example.instagram.services.pagination.paging_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.Post;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.pagination.PaginationCurrentForAllPosts;
import com.example.instagram.services.pagination.PagingAdapter;
import com.example.instagram.services.pagination.adapters.PaginationViewPosts;

import org.json.JSONException;
import org.json.JSONObject;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingAdapterPosts extends PagingAdapter {
    private PaginationViewPosts paginationAdapter;
    private final JSONObject jsonObject;
    private boolean isStart = true;
    public static boolean isEnd = false;

    public PagingAdapterPosts(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity, JSONObject jsonObject) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);
        this.jsonObject = jsonObject;
        isEnd = false;
        PaginationCurrentForAllPosts.resetCurrent();

        jsonObject.put("from", Integer.toString(PaginationCurrentForAllPosts.current));
        jsonObject.put("amount", Integer.toString(PaginationCurrentForAllPosts.amountOfPagination));

        // initialise adapter
        paginationAdapter = new PaginationViewPosts(activity, context, postsLibrary, this);
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
    public void notifyAdapterToClearAll() {
        paginationAdapter.getPostsLibrary().getDataArrayList().clear();
        paginationAdapter.notifyDataSetChanged();

        PagingAdapterPosts.isEnd = false;
        PaginationCurrentForAllPosts.resetCurrent();
    }
    // endregion

    @Override
    protected void setPaginationAdapter() {
        paginationAdapter = new PaginationViewPosts(activity, context, postsLibrary, this);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData() throws JSONException {
        if (!PagingAdapterPosts.isEnd) {
            startSkeletonAnim();

            // if it start of find
            if (!isStart) {
                jsonObject.put("from", Integer.toString(PaginationCurrentForAllPosts.current));
                jsonObject.put("amount", Integer.toString(PaginationCurrentForAllPosts.amountOfPagination));
            }

            isStart = false;

            new DoCallBack().setValues(() -> {
                isBusy = false;
                PaginationCurrentForAllPosts.nextCurrent();
                setPaginationAdapter();
            }, context, new Object[]{jsonObject, postsLibrary, (Runnable) this::stopSkeletonAnim}).sendToFindPost();
        }
    }
}
