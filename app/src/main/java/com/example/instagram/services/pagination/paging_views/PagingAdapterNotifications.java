package com.example.instagram.services.pagination.paging_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.PaginationCurrentForAllNotifications;
import com.example.instagram.services.pagination.PagingAdapter;
import com.example.instagram.services.pagination.adapters.PaginationViewNotifications;

import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingAdapterNotifications extends PagingAdapter {
    private PaginationViewNotifications paginationAdapter;
    public static boolean isEnd = false;

    public PagingAdapterNotifications(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity) {
        super(scrollView, recyclerView, shimmerLayout, context, activity);

        isEnd = false;
        PaginationCurrentForAllNotifications.resetCurrent();

        // initialise adapter
        paginationAdapter = new PaginationViewNotifications(activity, context, notificationsLibrary, this);
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
    public void notifyAllLibrary() {
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearAll() {
        paginationAdapter.getNotificationsLibrary().getNotifications().clear();
        paginationAdapter.notifyDataSetChanged();
        PagingAdapterNotifications.isEnd = false;

        try {
            getData();
        } catch (JSONException e) {
            Log.d("sendToGetAllPosts: ", e.getMessage());
        }
    }
    //endregion

    @Override
    protected void setPaginationAdapter() {
        paginationAdapter = new PaginationViewNotifications(activity, context, notificationsLibrary, this);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData() throws JSONException {
        if (!PagingAdapterNotifications.isEnd) {
            startSkeletonAnim();

            String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
            new DoCallBack().setValues(() -> {
                isBusy = false;
                PaginationCurrentForAllNotifications.nextCurrent();
                setPaginationAdapter();
            }, context, new Object[]{login, PaginationCurrentForAllComments.current, PaginationCurrentForAllComments.amountOfPagination, notificationsLibrary, (Runnable) this::stopSkeletonAnim}).sendToGetNotifications();
        }
    }
}
