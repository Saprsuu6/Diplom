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
        Post data1 = postsLibrary.getDataArrayList().get(position);
        Post data2 = postsLibrary.getDataArrayList().get(position + 1);
        Post data3 = postsLibrary.getDataArrayList().get(position + 2);

        // prepare all posts in row
        ImageView image1 = setImage(data1, holder);
        ImageView image2 = setImage(data2, holder);
        ImageView image3 = setImage(data3, holder);

        // set size for cells
        holder.cellsContainerParams.height = size.x / 3;

        holder.cellsContainer.addView(image1);
        holder.cellsContainer.addView(image2);
        holder.cellsContainer.addView(image3);

        image1.setOnClickListener(postInDialog());
        image2.setOnClickListener(postInDialog());
        image3.setOnClickListener(postInDialog());
    }

    private ImageView setImage(Post data, @NonNull ViewHolderPostsCells holder) {
        ImageView image = new ImageView(context);
        image.setLayoutParams(holder.cellsContainerParams);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setPadding(2, 2, 0, 2);

        assert data.getResourceImg() != null;
        if (data.getResourceImg().equals("")) {
            // set image
            Glide.with(context).load(data.getResourceVideo())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);

        } else {
            Glide.with(context).load(data.getResourceImg())
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(image);
        }

        return image;
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
