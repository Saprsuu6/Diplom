package com.example.instagram.services.pagination.paging_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.main_process.UserPage;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.pagination.PaginationCurrentForAllPostsInCells;
import com.example.instagram.services.pagination.PagingAdapter;
import com.example.instagram.services.pagination.adapters.PaginationViewPostsCells;

import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingAdapterPostsCells extends PagingAdapter {
    private PaginationViewPostsCells paginationAdapter;
    public static boolean isEnd = false;

    public PagingAdapterPostsCells(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);

        isEnd = false;
        PaginationCurrentForAllPostsInCells.resetCurrent();

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
        paginationAdapter = new PaginationViewPostsCells(context, activity, postsLibrary, this);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData() throws JSONException {
        if (!PagingAdapterPostsCells.isEnd) {
            startSkeletonAnim();

            new DoCallBack().setValues(() -> {
                isBusy = false;
                PaginationCurrentForAllPostsInCells.nextCurrent();
                setPaginationAdapter();
            }, context, new Object[]{PaginationCurrentForAllPostsInCells.current, PaginationCurrentForAllPostsInCells.amountOfPagination, UserPage.userPage.getLogin(), postsLibrary, (Runnable) this::stopSkeletonAnim}).sendToGetPostsOfUserInCells();
        }
    }
}
