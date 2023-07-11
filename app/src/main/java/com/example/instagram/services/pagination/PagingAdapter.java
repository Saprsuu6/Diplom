package com.example.instagram.services.pagination;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.CommentsLibrary;
import com.example.instagram.DAOs.NotificationsLibrary;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.DAOs.UsersLibrary;
import com.example.instagram.R;

import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;

abstract public class PagingAdapter {
    private final LinearLayout skeletonsLayout;
    private final ShimmerLayout shimmerLayout;
    protected final RecyclerView recyclerView;
    protected Boolean isBusy = false;

    @Nullable
    protected final Activity activity;
    protected final PostsLibrary postsLibrary = new PostsLibrary();
    protected final CommentsLibrary commentsLibrary = new CommentsLibrary();
    protected final UsersLibrary usersLibrary = new UsersLibrary();
    protected final NotificationsLibrary notificationsLibrary = new NotificationsLibrary();

    public PagingAdapter(NestedScrollView scrollView, RecyclerView recyclerView, ShimmerLayout shimmerLayout, @Nullable Activity activity) {
        this.activity = activity;
        this.recyclerView = recyclerView;
        this.shimmerLayout = shimmerLayout;

        // set layout manager
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));
        skeletonsLayout = (LinearLayout) shimmerLayout.getChildAt(0);

        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (!isBusy) {
                int positionLastChild = v.getChildAt(0).getMeasuredHeight();
                int height = v.getMeasuredHeight();

                int bottom = positionLastChild + height;
                if (scrollY == bottom) {
                    try {
                        isBusy = true;
                        getData();
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
        });
    }

    protected void startSkeletonAnim() {
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmerAnimation();
        skeletonsLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.anim_paging));
    }

    protected void stopSkeletonAnim() {
        shimmerLayout.stopShimmerAnimation();
        shimmerLayout.setVisibility(View.GONE);
    }

    abstract protected void getData() throws JSONException;

    abstract protected void setPaginationAdapter();
}
