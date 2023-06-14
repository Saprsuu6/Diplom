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

import com.example.instagram.services.Services;
import com.example.instagram.services.SharedPreferences;
import com.example.instagram.services.pagination.PagingView;
import com.example.instagram.services.pagination.adapters.PaginationAdapterPosts;

import org.json.JSONArray;
import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PagingViewGetAllPosts extends PagingView {
    private PaginationAdapterPosts paginationAdapter;
    private int lastPosition;
    private static final int paginationAmount = 3;
    private final LinearLayoutManager manager;

    public PagingViewGetAllPosts(NestedScrollView scrollView, RecyclerView recyclerView,
                                 ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity) throws JSONException {
        super(scrollView, recyclerView, shimmerLayout, context, activity);

        // initialise adapter
        paginationAdapter = new PaginationAdapterPosts(activity, context, postsLibrary);

        // set layout manager
        manager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(manager);

        // set adapter
        recyclerView.setAdapter(paginationAdapter);

        setListeners();

        try {
            resetAmountOfPosts();
        } catch (JSONException e) {
            Log.d("sendToGetAllPosts: ", e.getMessage());
        }
    }

    // region notifiers
    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearByPosition(int position) {
        paginationAdapter.deleteByPosition(position);
        paginationAdapter.notifyItemChanged(position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void notifyAdapterToClearAll() {
        paginationAdapter.getPostsLibrary().getDataArrayList().clear();
    }
    // endregion

    // region sva and load position
    public void loadPosition() {
        int position = SharedPreferences.loadIntSP(context, "lastNewsPosition");
        recyclerView.scrollToPosition(position);
    }

    public void savePosition() {
        SharedPreferences.saveSP(context, "lastNewsPosition", lastPosition);
    }

    private void setListeners() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                lastPosition = manager.findLastVisibleItemPosition();
            }
        });
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

    private void resetAmountOfPosts() throws JSONException {
        startSkeletonAnim();

        Services.sendToGetAllPosts(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body();

                    if (!body.equals("[]") && !body.equals("")) {
                        try {
                            postsLibrary.setDataArrayList(new JSONArray(body));
                            setPaginationAdapter();
                        } catch (JSONException e) {
                            Log.d("JSONException: ", e.getMessage());
                        }

                        // TODO not enough time
                        loadPosition();
                    }
                    else {
                        try {
                            getData();
                        } catch (JSONException e) {
                            Log.d("sendToGetAllPosts: ", e.getMessage());
                        }
                    }

                    stopSkeletonAnim();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("sendToGetAllPosts: ", t.getMessage());
            }
        }, 0, "current");
    }

    public void setToBegin()throws JSONException {
        startSkeletonAnim();

        Services.sendToGetAllPosts(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        getData();
                    } catch (JSONException e) {
                        Log.d("sendToGetAllPosts: ", e.getMessage());
                    }

                    stopSkeletonAnim();
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                Log.d("sendToGetAllPosts: ", t.getMessage());
            }
        }, PagingViewGetAllPosts.paginationAmount, "begin");
    }

    // begin - в начало end - в конец current - от начала до текущего
}
