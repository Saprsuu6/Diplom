package com.example.instagram.services.pagination.paging_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.main_process.UserPage;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.pagination.PaginationCurrentForAllPostsInCellsPosts;
import com.example.instagram.services.pagination.PaginationCurrentForAllPostsInCellsSaved;
import com.example.instagram.services.pagination.PagingAdapter;
import com.example.instagram.services.pagination.adapters.PaginationViewPostsCells;

import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingAdapterPostsCells extends PagingAdapter {
    private PaginationViewPostsCells paginationAdapter;
    public static boolean isEnd = false;
    private final Boolean isSaved;

    public PagingAdapterPostsCells(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity, Boolean isSaved) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);
        this.isSaved = isSaved;
        isEnd = false;
        PaginationCurrentForAllPostsInCellsPosts.resetCurrent();

        // initialise adapter
        paginationAdapter = new PaginationViewPostsCells(context, activity, postsLibrary, this);
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

        PagingAdapterPostsCells.isEnd = false;
        PaginationCurrentForAllPostsInCellsPosts.resetCurrent();

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
        paginationAdapter = new PaginationViewPostsCells(context, activity, postsLibrary, this);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData() throws JSONException {
        if (!PagingAdapterPostsCells.isEnd) {
            startSkeletonAnim();

            if (!isSaved) {
                new DoCallBack().setValues(() -> {
                    isBusy = false;
                    PaginationCurrentForAllPostsInCellsPosts.nextCurrent();
                    setPaginationAdapter();
                }, context, new Object[]{PaginationCurrentForAllPostsInCellsPosts.current, PaginationCurrentForAllPostsInCellsPosts.amountOfPagination, UserPage.userPage.getLogin(), postsLibrary, (Runnable) this::stopSkeletonAnim}).sendToGetPostsOfUserInCells();
            } else {
                String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
                new DoCallBack().setValues(() -> {
                    isBusy = false;
                    PaginationCurrentForAllPostsInCellsPosts.nextCurrent();
                    setPaginationAdapter();
                }, context, new Object[]{PaginationCurrentForAllPostsInCellsSaved.current, PaginationCurrentForAllPostsInCellsSaved.amountOfPagination, login, postsLibrary, (Runnable) this::stopSkeletonAnim}).getSavedPosts();
            }
        }
    }
}
