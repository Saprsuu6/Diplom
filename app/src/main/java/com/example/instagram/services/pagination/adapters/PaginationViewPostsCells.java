package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.graphics.Point;
import android.net.Uri;
import android.os.Handler;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.UserPage;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.GetMediaLink;
import com.example.instagram.services.PostInDialog;
import com.example.instagram.services.SetImagesGlide;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPostsCells;
import com.google.android.flexbox.FlexboxLayout;

import org.json.JSONException;
import org.json.JSONObject;

public class PaginationViewPostsCells extends RecyclerView.Adapter<PaginationViewPostsCells.ViewHolderPostsCells> {
    private final Activity activity;
    private final PostsLibrary postsLibrary;
    private final Point size;
    private final PagingAdapterPostsCells pagingView;
    private final PostInDialog postInDialog = new PostInDialog();

    public PostsLibrary getPostsLibrary() {
        return postsLibrary;
    }

    public PaginationViewPostsCells(Activity activity, PostsLibrary postsLibrary, PagingAdapterPostsCells pagingView) {
        this.postsLibrary = postsLibrary;
        this.pagingView = pagingView;
        this.activity = activity;

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

                LinearLayout layout = new LinearLayout(activity);
                CardView cardView = new CardView(activity);

                // region set media content
                if (mime.contains(activity.getString(R.string.mime_image))) {
                    //set image
                    content = setImage(data);
                    cardView.setLayoutParams(holder.cellParams);
                } else if (mime.contains(activity.getString(R.string.mime_video))) {
                    // set image
                    content = setVideo(data);
                    layout.setLayoutParams(holder.cellParams);
                    layout.setGravity(Gravity.CENTER);
                    cardView.setLayoutParams(holder.mediaParams);
                } else if (mime.contains(activity.getString(R.string.mime_audio))) {
                    // set audio
                    content = setImageForAudio();
                    cardView.setLayoutParams(holder.cellParams);
                }
                // endregion

                if (content != null) {
                    cardView.addView(content);
                    cardView.setRadius(50);

                    layout.addView(cardView);
                    holder.flexboxLayout.addView(layout);

                    content.setOnClickListener(v -> {
                        try {
                            postInDialog(data);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            } else {
                break;
            }
        }

        holder.flexboxLayout.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.anim_paging));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private ImageView setImageForAudio() {
        ImageView image = new ImageView(activity);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        image.setImageDrawable(activity.getDrawable(R.drawable.play_cell));

        return image;
    }

    private ImageView setImage(Post data) {
        ImageView image = new ImageView(activity);
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String link = GetMediaLink.getMediaLink(activity, data.getResourceMedia());
        SetImagesGlide.setImageGlide(activity, link, image);

        return image;
    }

    private VideoView setVideo(Post data) {
        VideoView video = new VideoView(activity);
        String link = GetMediaLink.getMediaLink(activity, data.getResourceMedia());
        Uri videoUri = Uri.parse(link);
        video.setVideoURI(videoUri);

        video.start();
        video.setOnPreparedListener(mp -> mp.setLooping(true));
        video.requestFocus();

        return video;
    }

    @SuppressLint("SetTextI18n")
    private void postInDialog(Post post) throws JSONException {
        Dialog builder = postInDialog.getPostDialog(activity, post);
        String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());
        String token = Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString());

        if (UserPage.userPage != null) {
            if (login.equals(UserPage.userPage.getLogin())) {
                Button delete = postInDialog.getDelete();
                Button close = postInDialog.getClose();
                delete.setVisibility(View.VISIBLE);
                delete.setOnClickListener(v -> {
                    Handler handler = new Handler();
                    final int[] timer = {11};
                    Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            if (timer[0] == 1) {
                                try {
                                    JSONObject jsonObject = Post.getJSONToDeletePost(post.getPostId(), token);
                                    // delete post
                                    new DoCallBack().setValues(pagingView::notifyAdapterToClearAll, activity, new Object[]{jsonObject}).sendToDeletePost();
                                } catch (JSONException e) {
                                    throw new RuntimeException(e);
                                }

                                handler.removeCallbacks(this);
                                delete.setText(activity.getString(R.string.remove_post));
                                if(postInDialog.getRunnable() != null) postInDialog.getRunnable().run();
                            }
                            timer[0]--;
                            delete.setText(timer[0] + " " + activity.getString(R.string.remove_post));
                            handler.postDelayed(this, 1000L);
                        }
                    };

                    handler.postDelayed(runnable, 1000L);

                    delete.setOnClickListener(v1 -> {
                        handler.removeCallbacks(runnable);
                        try {
                            JSONObject jsonObject = Post.getJSONToDeletePost(post.getPostId(), token);
                            // delete post
                            new DoCallBack().setValues(pagingView::notifyAdapterToClearAll, activity, new Object[]{jsonObject}).sendToDeletePost();
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        builder.dismiss();
                    });

                    close.setOnClickListener(v12 -> {
                        handler.removeCallbacks(runnable);
                        delete.setText(activity.getString(R.string.remove_post));
                        if(postInDialog.getRunnable() != null) postInDialog.getRunnable().run();
                    });
                });
            }
        }

        builder.show();
    }

    @Override
    public int getItemCount() {
        return postsLibrary.getDataArrayList().size() >= 1 ? 1 : 0;
    }

    public class ViewHolderPostsCells extends RecyclerView.ViewHolder {
        private final FlexboxLayout flexboxLayout;
        private final LinearLayout.LayoutParams mediaParams;
        private final LinearLayout.LayoutParams cellParams;

        public ViewHolderPostsCells(@NonNull View itemView) {
            super(itemView);

            flexboxLayout = itemView.findViewById(R.id.layout);
            mediaParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            cellParams = new LinearLayout.LayoutParams(size.x / 4, size.x / 4);
            cellParams.setMargins(20, 20, 20, 20);
        }
    }
}
