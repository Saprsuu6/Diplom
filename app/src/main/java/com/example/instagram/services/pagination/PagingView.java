package com.example.instagram.services.pagination;

import android.app.Activity;
import android.view.Display;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.MainDataLibrary;
import com.example.instagram.services.pagination.adapters.PaginationAdapterUsers;

import org.json.JSONArray;
import org.json.JSONException;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

abstract public class PagingView {
    protected ShimmerLayout shimmerLayout;
    protected int page;
    protected final int scrollOffset = 5;
    protected final RecyclerView recyclerView;
    protected final int onePageLimit;
    protected final Activity activity;
    protected final MainDataLibrary mainDataLibrary = new MainDataLibrary();

    public PagingView(NestedScrollView scrollView, RecyclerView recyclerView,
                      ShimmerLayout shimmerLayout, Activity activity,
                      int page, int onePageLimit) {
        this.activity = activity;
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
                getData(this.page, this.onePageLimit);
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

    protected final void getData(int page, int onePageLimit) {
        startSkeletonAnim();

        // initialise retrofit
        String link = "https://picsum.photos";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(link)
                .addConverterFactory(ScalarsConverterFactory.create())
                .build();

        // create main interface
        PagingRequest mainInterface = retrofit.create(PagingRequest.class);

        // initialize call
        Call<String> call = mainInterface.STRING_CALL(page, onePageLimit);
        call.enqueue(new Callback<String>() {
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

            }
        });

    }

    abstract protected void setPaginationAdapter();
}
