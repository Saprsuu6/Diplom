package com.example.instagram.services.pagination;

import android.app.Activity;
import android.content.Context;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.PostsLibrary;

import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;

abstract public class PagingView {
    protected ShimmerLayout shimmerLayout;
    protected int page;
    protected final int scrollOffset = 5;
    protected final RecyclerView recyclerView;
    protected final int onePageLimit;
    protected final Context context;

    @Nullable
    protected final Activity activity;
    protected final PostsLibrary postsLibrary = new PostsLibrary();

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
                    getData(page, onePageLimit);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }
            }
        });
    }

    protected void startSkeletonAnim() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
    }

    protected void stopSkeletonAnim() {
        shimmerLayout.stopShimmerAnimation();
        shimmerLayout.setVisibility(View.GONE);
    }

    abstract protected void getData(int page, int onePageLimit) throws JSONException;

    abstract protected void setPaginationAdapter();
}
