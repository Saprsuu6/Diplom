package com.example.instagram.services.pagination.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.R;
import com.example.instagram.services.PostInDialog;

public class PaginationAdapterPostsCells extends RecyclerView.Adapter<PaginationAdapterPostsCells.ViewHolderPostsCells> {
    @Nullable
    private final Activity activity;
    private final Context context;
    private final PostsLibrary postsLibrary;
    private final Point size;

    public PaginationAdapterPostsCells(Context context, Activity activity, PostsLibrary postsLibrary) {
        this.activity = activity;
        this.context = context;
        this.postsLibrary = postsLibrary;

        Display display = activity.getWindowManager().getDefaultDisplay();
        size = new Point();
        display.getSize(size);
    }

    @NonNull
    @Override
    public ViewHolderPostsCells onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_post_self_page_cell, parent, false); // don't forget to change

        return new PaginationAdapterPostsCells.ViewHolderPostsCells(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPostsCells holder, int position) {
        Post data = postsLibrary.getDataArrayList().get(position);

        // TODO: take 3 post

        // set image
        ImageView image1 = new ImageView(context);
        image1.setLayoutParams(holder.cellsContainerParams);
        image1.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image1.setPadding(0, 2, 2, 2);

        assert data.getResourceImg() != null;
        if (data.getResourceImg().equals("")) {
            // set image
            Glide.with(context).load(data.getResourceVideo())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image1);

        }
        else {
            Glide.with(context).load(data.getResourceImg())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image1);
        }

        ImageView image2 = new ImageView(context);
        image2.setLayoutParams(holder.cellsContainerParams);
        image2.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image2.setPadding(2, 2, 2, 2);
        if (data.getResourceImg().equals("")) {
            // set image
            Glide.with(context).load(data.getResourceVideo())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image2);

        }
        else {
            Glide.with(context).load(data.getResourceImg())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image2);
        }

        ImageView image3 = new ImageView(context);
        image3.setLayoutParams(holder.cellsContainerParams);
        image3.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image3.setPadding(2, 2, 0, 2);
        if (data.getResourceImg().equals("")) {
            // set image
            Glide.with(context).load(data.getResourceVideo())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image3);

        }
        else {
            Glide.with(context).load(data.getResourceImg())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image3);
        }

        holder.cellsContainerParams.height = size.x / 3;

        holder.cellsContainer.addView(image1);
        holder.cellsContainer.addView(image2);
        holder.cellsContainer.addView(image3);

        image1.setOnClickListener(postInDialog());
        image2.setOnClickListener(postInDialog());
        image3.setOnClickListener(postInDialog());
    }

    private View.OnClickListener postInDialog() {
        // TODO: load post info
        return v -> PostInDialog.getPostDialog(context, context.getResources()).show();
    }

    @Override
    public int getItemCount() {
        return postsLibrary.getDataArrayList().size();
    }

    // region SharedPreferences
    //Сохраняет флажок в SharedPreferences
    public void saveSP(String key, boolean value) {
        com.example.instagram.services.SharedPreferences.saveSP(context, key, value);
    }

    //Загружает нажатый флажок из SharedPreferences
    public boolean loadSP(String key) {
        return com.example.instagram.services.SharedPreferences.loadBoolSP(context, key);
    }

    //Удаляет нажатый флажок из SharedPreferences
    public void deleteSp(String key) {
        com.example.instagram.services.SharedPreferences.deleteSP(context, key);
    }
    // endregion SharedPreferences

    public class ViewHolderPostsCells extends RecyclerView.ViewHolder {
        private final LinearLayout cellsContainer;
//        private final int likes;
//        private final String description;
//        private final String hAgo;
        private final LinearLayout.LayoutParams cellsContainerParams;

        public ViewHolderPostsCells(@NonNull View itemView) {
            super(itemView);

            cellsContainer = itemView.findViewById(R.id.cells_container);

            cellsContainerParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT // TODO: change to dynamic value
            );
            cellsContainerParams.weight = 1;
        }
    }
}
