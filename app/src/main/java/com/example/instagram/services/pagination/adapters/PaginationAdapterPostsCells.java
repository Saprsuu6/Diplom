package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.SelfPage;
import com.example.instagram.services.AudioController;
import com.example.instagram.services.Errors;
import com.example.instagram.services.PostInDialog;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPostsInCells;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaginationAdapterPostsCells extends RecyclerView.Adapter<PaginationAdapterPostsCells.ViewHolderPostsCells> {
    @Nullable
    private final Activity activity;
    private final Context context;
    private final PostsLibrary postsLibrary;
    private final Point size;
    private PagingViewGetAllPostsInCells pagingView;
    private final PostInDialog postInDialog = new PostInDialog();

    public PostsLibrary getPostsLibrary() {
        return postsLibrary;
    }

    public PaginationAdapterPostsCells(Context context, @Nullable Activity activity, PostsLibrary postsLibrary, PagingViewGetAllPostsInCells pagingView) {
        this.activity = activity;
        this.context = context;
        this.postsLibrary = postsLibrary;
        this.pagingView = pagingView;

        size = new Point();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            display.getSize(size);
        }
    }

    @NonNull
    @Override
    public ViewHolderPostsCells onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post_self_page_cell, parent, false); // don't forget to change
        return new PaginationAdapterPostsCells.ViewHolderPostsCells(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderPostsCells holder, int position) {
        for (int i = 0; i < PaginationCurrentForAllComments.amountOfPagination; i++) {
            if (i < postsLibrary.getDataArrayList().size()) {
                Post data = postsLibrary.getDataArrayList().get(i);
                View content = null;

                String mime = data.getMimeType();

                // region set media content
                if (mime.contains(context.getString(R.string.mime_image))) {
                    //set image
                    content = setImage(data, holder);
                } else if (mime.contains(context.getString(R.string.mime_video))) {
                    // set image
                    content = setVideo(data, holder);
                } else if (mime.contains(context.getString(R.string.mime_audio))) {
                    // set audio
                    content = setImageForAudio(holder);
                }
                // endregion

                holder.flexboxLayout.addView(content);
                if (content != null) content.setOnClickListener(postInDialog(data));
            } else {
                break;
            }
        }

        holder.flexboxLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_paging));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ImageView setImageForAudio(@NonNull ViewHolderPostsCells holder) {
        ImageView image = new ImageView(context);
        image.setLayoutParams(holder.mediaParams);
        image.setPadding(3, 3, 3, 3);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setImageDrawable(context.getDrawable(R.drawable.play_cell));

        return image;
    }

    private ImageView setImage(Post data, @NonNull ViewHolderPostsCells holder) {
        ImageView image = new ImageView(context);
        image.setLayoutParams(holder.mediaParams);
        image.setPadding(3, 3, 3, 3);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String imagePath = Services.BASE_URL + context.getString(R.string.root_folder) + data.getResourceMedia();

        Glide.with(context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);

        return image;
    }

    private VideoView setVideo(Post data, @NonNull ViewHolderPostsCells holder) {
        VideoView video = new VideoView(context);
        video.setLayoutParams(holder.mediaParams);
        video.setPadding(3, 3, 3, 3);

        String videoPath = Services.BASE_URL + context.getString(R.string.root_folder) + data.getResourceMedia();
        Uri videoUri = Uri.parse(videoPath);
        video.setVideoURI(videoUri);

        video.start();
        video.requestFocus();

        return video;
    }

    private View.OnClickListener postInDialog(Post post) {
        AlertDialog.Builder builder = postInDialog.getPostDialog(context, post, pagingView);

        if (TransitUser.user.getLogin().equals(SelfPage.userPage.getLogin())) {
            builder.setNegativeButton(context.getApplicationContext().getString(R.string.remove_post), (dialog, which) -> {
                AlertDialog.Builder negativeButton = new AlertDialog.Builder(context).setMessage(context.getApplicationContext().getString(R.string.remove_post_question)).setPositiveButton(context.getApplicationContext().getString(R.string.yes), (dialog1, which1) -> {
                    TransitPost.postsToDeleteFromOtherPage.add(post);
                    JSONObject body = new JSONObject();

                    try {
                        body.put("postId", post.getPostId());
                        body.put("token", TransitUser.user.getToken());

                        Services.sendToDeletePost(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String responseStr = response.body();
                                    Errors.delete(context, responseStr).show();

                                    SelfPage.userPage.setAmountPosts(SelfPage.userPage.getAmountPosts() - 1);
                                    pagingView.notifyAdapterToClearAll();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.d("onFailure: (sendToDeletePost)", t.getMessage());
                            }
                        }, body.toString());
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }).setNegativeButton(context.getApplicationContext().getString(R.string.no), null);
                negativeButton.show();
            });
        }

        return v -> builder.show();
    }

    @Override
    public int getItemCount() {
        return postsLibrary.getDataArrayList().size() >= 1 ? 1 : 0;
    }

    public class ViewHolderPostsCells extends RecyclerView.ViewHolder {
        private final FlexboxLayout flexboxLayout;
        private final LinearLayout.LayoutParams mediaParams;

        public ViewHolderPostsCells(@NonNull View itemView) {
            super(itemView);

            flexboxLayout = itemView.findViewById(R.id.layout);
            mediaParams = new LinearLayout.LayoutParams(size.x / 3, size.x / 3);
        }
    }
}
