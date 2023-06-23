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

import com.example.instagram.DAOs.Comment;
import com.example.instagram.main_process.Comments;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.Services;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.PaginationCurrentForAllPosts;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterComments;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagingViewGetAllComments extends PagingView {
    private PaginationAdapterComments paginationAdapter;
    public static boolean isEnd = false;

    public PagingViewGetAllComments(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity) {
        super(scrollView, recyclerView, shimmerLayout, context, activity);
        isEnd = false;
        PaginationCurrentForAllComments.resetCurrent();

        // initialise adapter
        paginationAdapter = new PaginationAdapterComments(activity, context, commentsLibrary);

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
    public void notifyAdapterToClearByPosition(List<String> deleteId) {
        if (deleteId != null) {
            paginationAdapter.getCommentsLibrary().getCommentList().removeIf(commentToDelete -> deleteId.stream().anyMatch(item -> item.equals(commentToDelete.getCommentId())));
            List<Comment> comments = paginationAdapter.getCommentsLibrary().getCommentList();
            paginationAdapter.notifyDataSetChanged();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyLibraryByPosition(int position, String text) {
        paginationAdapter.getCommentsLibrary().getCommentList().get(position).setContent(text);
        paginationAdapter.notifyItemChanged(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAllLibrary() {
        paginationAdapter.notifyDataSetChanged();
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
        paginationAdapter = new PaginationAdapterComments(activity, context, commentsLibrary);
        recyclerView.setAdapter(paginationAdapter);
    }

    @Override
    protected void getData() throws JSONException {
        if (!PagingViewGetAllComments.isEnd) {
            startSkeletonAnim();

            Services.sendToGetAllComments(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllComments.amountOfPagination) {
                                    PagingViewGetAllComments.isEnd = true;
                                }

                                isBusy = false;
                                PaginationCurrentForAllComments.nextCurrent();
                                commentsLibrary.setDataArrayList(jsonArray);
                                setPaginationAdapter();
                            } catch (JSONException | ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        stopSkeletonAnim();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllComments: ", t.getMessage());
                }
            }, PaginationCurrentForAllComments.current, PaginationCurrentForAllComments.amountOfPagination, NewsLine.mapPost.second.getPostId());
        }
    }
}
