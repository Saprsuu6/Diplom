package com.example.instagram.services.pagination;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.MainDataLibrary;
import com.example.instagram.services.MyRetrofit;
import com.example.instagram.services.Services;

import org.json.JSONArray;
import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

abstract public class PagingView {
    protected ShimmerLayout shimmerLayout;
    protected int page;
    protected final int scrollOffset = 5;
    protected final RecyclerView recyclerView;
    protected final int onePageLimit;
    protected final Context context;

    @Nullable
    protected final Activity activity;
    protected final MainDataLibrary mainDataLibrary = new MainDataLibrary();

    public PagingView(NestedScrollView scrollView, RecyclerView recyclerView,
                      ShimmerLayout shimmerLayout, Context context, @Nullable Activity activity,
                      int page, int onePageLimit) {
        this.activity = activity;
        this.context = context;
        this.recyclerView = recyclerView;
        this.page = page;
        this.onePageLimit = onePageLimit;
        this.shimmerLayout = shimmerLayout;

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int positionLastChild = v.getChildAt(0).getMeasuredHeight();
            int height = v.getMeasuredHeight();

            int bottom = positionLastChild - height;
            int bottomWithOffset = bottom / scrollOffset;

            if (scrollY == bottom || bottomWithOffset >= oldScrollY && bottomWithOffset <= scrollY) { // when rich last item position
                this.page++;
                try {
                    getData(this.page, this.onePageLimit);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    private void startSkeletonAnim() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
    }

    private void stopSkeletonAnim() {
        shimmerLayout.stopShimmerAnimation();
        shimmerLayout.setVisibility(View.GONE);
    }

    protected void getData(int page, int onePageLimit) throws JSONException {
        startSkeletonAnim();

        Services.sendToGetPosts(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    stopSkeletonAnim();

                    // add new data
                    String body = response.body();
                    try {
                        mainDataLibrary.setDataArrayList(new JSONArray(body));

                        setPaginationAdapter();
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                System.out.println(t.getMessage());
            }
        }, page, onePageLimit);
    }

    abstract protected void setPaginationAdapter();
}
