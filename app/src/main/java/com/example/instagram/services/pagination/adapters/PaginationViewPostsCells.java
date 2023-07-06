package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Point;
import android.net.Uri;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.UserPage;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.PostInDialog;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPostsCells;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class PaginationViewPostsCells extends RecyclerView.Adapter<PaginationViewPostsCells.ViewHolderPostsCells> {
    private final Context context;
    private final PostsLibrary postsLibrary;
    private final Point size;
    private final PagingAdapterPostsCells pagingView;
    private final PostInDialog postInDialog = new PostInDialog();

    public PostsLibrary getPostsLibrary() {
        return postsLibrary;
    }

    public PaginationViewPostsCells(Context context, @Nullable Activity activity, PostsLibrary postsLibrary, PagingAdapterPostsCells pagingView) {
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
        return new PaginationViewPostsCells.ViewHolderPostsCells(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
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
                    content = setImage(data);
                } else if (mime.contains(context.getString(R.string.mime_video))) {
                    // set image
                    content = setVideo(data);
                } else if (mime.contains(context.getString(R.string.mime_audio))) {
                    // set audio
                    content = setImageForAudio();
                }
                // endregion

                if (content != null) {
                    CardView cardView = new CardView(context);
                    cardView.addView(content);
                    cardView.setLayoutParams(holder.mediaParams);
                    cardView.setRadius(50);
                    holder.flexboxLayout.addView(cardView);

                    try {
                        content.setOnClickListener(postInDialog(data));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                break;
            }
        }

        holder.flexboxLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_paging));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ImageView setImageForAudio() {
        ImageView image = new ImageView(context);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setImageDrawable(context.getDrawable(R.drawable.play_cell));

        return image;
    }

    private ImageView setImage(Post data) {
        ImageView image = new ImageView(context);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String imagePath = Services.BASE_URL + context.getString(R.string.root_folder) + data.getResourceMedia();

        Glide.with(context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(image);

        return image;
    }

    private VideoView setVideo(Post data) {
        VideoView video = new VideoView(context);
        String videoPath = Services.BASE_URL + context.getString(R.string.root_folder) + data.getResourceMedia();
        Uri videoUri = Uri.parse(videoPath);
        video.setVideoURI(videoUri);

        video.start();
        video.requestFocus();

        return video;
    }

    private View.OnClickListener postInDialog(Post post) throws JSONException {
        AlertDialog.Builder builder = postInDialog.getPostDialog(context, post, pagingView);
        String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
        String token = Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString());

        if (login.equals(UserPage.userPage.getLogin())) {
            builder.setNegativeButton(context.getApplicationContext().getString(R.string.remove_post), (dialog, which) -> {
                AlertDialog.Builder negativeButton = new AlertDialog.Builder(context).setMessage(context.getApplicationContext().getString(R.string.remove_post_question)).setPositiveButton(context.getApplicationContext().getString(R.string.yes), (dialog1, which1) -> {
                    TransitPost.postsToDeleteFromOtherPage.add(post);

                    try {
                        JSONObject jsonObject = Post.getJSONToDeletePost(post.getPostId(), token);
                        // delete post
                        new DoCallBack().setValues(() -> {
                            UserPage.userPage.setAmountPosts(UserPage.userPage.getAmountPosts() - 1);
                            pagingView.notifyAdapterToClearAll();
                        }, context, new Object[]{jsonObject}).deletePost();
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
            mediaParams.setMargins(5, 5, 5, 5);
        }
    }
}
