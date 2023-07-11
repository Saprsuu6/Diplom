package com.example.instagram.services.pagination.paging_views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Log;

import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.PagingAdapter;
import com.example.instagram.services.pagination.adapters.PaginationViewComments;

import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class PagingAdapterComments extends PagingAdapter {
    private PaginationViewComments paginationAdapter;
    public static boolean isEnd = false;

    public PagingAdapterComments(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Activity activity) {
        super(scrollView, recyclerView, shimmerLayout, activity);
        isEnd = false;
        PaginationCurrentForAllComments.resetCurrent();

        // initialise adapter
        paginationAdapter = new PaginationViewComments(activity, activity, commentsLibrary);
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
    public void notifyAdapterToClearByPosition(String commentId) {
        paginationAdapter.getCommentsLibrary().getCommentList().removeIf(commentToDelete -> commentToDelete.getCommentId().equals(commentId));
        paginationAdapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyLibraryByPosition(int position, String text) {
        paginationAdapter.getCommentsLibrary().getCommentList().get(position).setContent(text);
        paginationAdapter.notifyItemChanged(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearAll() {
        paginationAdapter.getCommentsLibrary().getCommentList().clear();
        paginationAdapter.notifyDataSetChanged();

        try {
            getData();
        } catch (JSONException e) {
            Log.d("sendToGetAllPosts: ", e.getMessage());
        }
    }
    //endregion

    @Override
    protected void setPaginationAdapter() {
        paginationAdapter = new PaginationViewComments(activity, activity, commentsLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData() throws JSONException {
        if (!PagingAdapterComments.isEnd) {
            startSkeletonAnim();

            new DoCallBack().setValues(() -> {
                isBusy = false;
                PaginationCurrentForAllComments.nextCurrent();
                setPaginationAdapter();
            }, activity, new Object[]{PaginationCurrentForAllComments.current, PaginationCurrentForAllComments.amountOfPagination, NewsLine.mapPost.second.getPostId(), commentsLibrary, (Runnable) this::stopSkeletonAnim}).sendToGetAllComments();
        }
    }
}
