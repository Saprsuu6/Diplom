package com.example.instagram.services.pagination.paging_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.pagination.PaginationCurrentForAllUsers;
import com.example.instagram.services.pagination.PagingAdapter;
import com.example.instagram.services.pagination.adapters.PaginationViewUsers;

import org.json.JSONException;
import org.json.JSONObject;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingAdapterUsers extends PagingAdapter {
    private final Activity activity;
    private final JSONObject jsonObject;
    private PaginationViewUsers paginationAdapter;
    public static boolean isEnd = false;
    private boolean isStart = true;
    private final Boolean isSubscribers;

    public PagingAdapterUsers(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Activity activity, JSONObject jsonObject, @Nullable Boolean isSubscribers) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, activity);
        this.jsonObject = jsonObject;
        this.isSubscribers = isSubscribers;
        this.activity = activity;
        
        isEnd = false;
        PaginationCurrentForAllUsers.resetCurrent();

        jsonObject.put("from", Integer.toString(PaginationCurrentForAllUsers.current));
        jsonObject.put("amount", Integer.toString(PaginationCurrentForAllUsers.amountOfPagination));

        // initialise adapter
        paginationAdapter = new PaginationViewUsers(activity, usersLibrary);
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
        paginationAdapter = new PaginationViewUsers(activity, usersLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }

    // region notifiers
    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearAll() {
        paginationAdapter.getUsersLibrary().getDataArrayList().clear();
        paginationAdapter.notifyDataSetChanged();

        PagingAdapterUsers.isEnd = false;
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
        if (!PagingAdapterUsers.isEnd) {
            startSkeletonAnim();

            // if it start of find
            if (!isStart) {
                jsonObject.put("from", Integer.toString(PaginationCurrentForAllUsers.current));
                jsonObject.put("amount", Integer.toString(PaginationCurrentForAllUsers.amountOfPagination));
            }

            isStart = false;

            Runnable runnable = () -> {
                isBusy = false;
                PaginationCurrentForAllUsers.nextCurrent();
                setPaginationAdapter();
            };

            if (isSubscribers == null) {
                new DoCallBack().setValues(runnable, activity, new Object[]{jsonObject, usersLibrary, (Runnable) this::stopSkeletonAnim}).sendToFindUser();
            } else if (isSubscribers) {
                new DoCallBack().setValues(runnable, activity, new Object[]{jsonObject, usersLibrary, (Runnable) this::stopSkeletonAnim}).sendToGetSubscribers();
            } else {
                new DoCallBack().setValues(runnable, activity, new Object[]{jsonObject, usersLibrary, (Runnable) this::stopSkeletonAnim}).sendToGetSubscribing();
            }
        }
    }
}
