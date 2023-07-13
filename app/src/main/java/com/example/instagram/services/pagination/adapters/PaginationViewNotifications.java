package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Comment;
import com.example.instagram.DAOs.Notification;
import com.example.instagram.DAOs.NotificationsLibrary;
import com.example.instagram.DAOs.Post;
import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.GetMediaLink;
import com.example.instagram.services.Intents;
import com.example.instagram.services.PostInDialog;
import com.example.instagram.services.Resources;
import com.example.instagram.services.SetImagesGlide;
import com.example.instagram.services.pagination.paging_views.PagingAdapterNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class PaginationViewNotifications extends RecyclerView.Adapter<PaginationViewNotifications.ViewHolderNotification> {
    private final Activity activity;
    private final NotificationsLibrary notificationsLibrary;
    private final PagingAdapterNotifications pagingView;
    private final PostInDialog postInDialog = new PostInDialog();
    public static Post publicPost;
    public static Comment publicComment;

    public NotificationsLibrary getNotificationsLibrary() {
        return notificationsLibrary;
    }

    public PaginationViewNotifications(Activity activity, NotificationsLibrary notificationsLibrary, PagingAdapterNotifications pagingView) {
        this.activity = activity;
        this.pagingView = pagingView;
        this.notificationsLibrary = notificationsLibrary;
    }

    @NonNull
    @Override
    public PaginationViewNotifications.ViewHolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
        return new ViewHolderNotification(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotification holder, int position) {
        Notification data = notificationsLibrary.getNotifications().get(position);

        // region send request to get avatar
        String avaLink = Cache.loadStringSP(activity, data.getAuthor()+".ava");
        if (!avaLink.equals("")) {
            Glide.with(activity.getApplicationContext()).load(avaLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.userAvatar);
        } else {
            try {
                new DoCallBack().setValues(null, activity, new Object[]{data.getUser(), holder.userAvatar}).sendToGetAvaImage();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        // endregion

        try {
            setResources(holder, data);
        } catch (JSONException | ParseException e) {
            throw new RuntimeException(e);
        }

        Resources.setText(DateFormatting.formatDate(data.getDate()), holder.date);
        Resources.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.anim_paging), holder.notificationLayout);
    }

    private void setResources(@NonNull ViewHolderNotification holder, Notification data) throws JSONException, ParseException {
        String message = activity.getResources().getString(R.string.user_notification);
        message += " " + data.getUser();

        switch (data.getAction()) {
            case "subscribe":
                message += " " + activity.getResources().getString(R.string.subscribe_notification);
                break;
            case "unsubscribe":
                message += " " + activity.getResources().getString(R.string.unsubscribe_notification);
                break;
            case "like":
                message += " " + activity.getResources().getString(R.string.like_notification);
                setMediaContent(holder, data);
                break;
            case "save":
                message += " " + activity.getResources().getString(R.string.save_notification);
                setMediaContent(holder, data);
                break;
            case "tage":
                message += " " + activity.getResources().getString(R.string.tag_notification);
                setMediaContent(holder, data);
                break;
            case "comment":
                holder.comment.setVisibility(View.VISIBLE);
                message += " " + activity.getResources().getString(R.string.comment_notification);
                setCommentContent(holder, data);
                setMediaContent(holder, data);
                break;
            case "response":
                holder.comment.setVisibility(View.VISIBLE);
                message += " " + activity.getResources().getString(R.string.reply_notification);
                setCommentContent(holder, data);
                setMediaContent(holder, data);
                break;
        }

        Resources.setText(message, holder.message);
        Resources.setEllipsize(TextUtils.TruncateAt.END, holder.comment);
        holder.comment.setOnClickListener(v -> {
            holder.comment.setMaxLines(holder.comment.getMaxLines() == 1 ? 100 : 1);
        });

        holder.userAvatar.setOnClickListener(v -> {
            try {
                new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{data.getAuthor()}).sendToGetCurrentUser();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        holder.message.setOnClickListener(v -> {
            try {
                new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{data.getAuthor()}).sendToGetCurrentUser();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setMediaContent(@NonNull ViewHolderNotification holder, Notification data) {
        if (data.getPostId() != null) {
            new DoCallBack().setValues(() -> {
                String mime = PaginationViewNotifications.publicPost.getMimeType();
                String link = GetMediaLink.getMediaLink(activity, PaginationViewNotifications.publicPost.getResourceMedia());

                // region set media content
                if (mime.contains(activity.getString(R.string.mime_image))) {
                    //set image
                    Resources.setVisibility(View.VISIBLE, holder.image);
                    SetImagesGlide.setImageGlide(activity, link, holder.image);
                    holder.image.setOnClickListener(v -> {
                        try {
                            postInDialog(PaginationViewNotifications.publicPost);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else if (mime.contains(activity.getString(R.string.mime_video))) {
                    // set video
                    Resources.setVisibility(View.VISIBLE, holder.video);
                    Uri videoUri = Uri.parse(link);
                    holder.video.setVideoURI(videoUri);
                    holder.video.setOnPreparedListener(mp -> mp.setLooping(true));
                    holder.video.start();
                    holder.video.findFocus();

                    holder.video.setOnClickListener(v -> {
                        try {
                            postInDialog(PaginationViewNotifications.publicPost);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } else if (mime.contains(activity.getString(R.string.mime_audio))) {
                    // set audio
                    Resources.setVisibility(View.VISIBLE, holder.audio);
                    Resources.setDrawableIntoImageView(activity.getDrawable(R.drawable.play_cell), holder.audio);
                    holder.audio.setScaleType(ImageView.ScaleType.CENTER_CROP);

                    holder.audio.setOnClickListener(v -> {
                        try {
                            postInDialog(PaginationViewNotifications.publicPost);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }, activity, new Object[]{data.getPostId()}).sendToGetPostById();
        }
    }

    private void setCommentContent(@NonNull ViewHolderNotification holder, Notification data) {
        if (data.getCommentId() != null) {
            new DoCallBack().setValues(() -> {
                Resources.setVisibility(View.VISIBLE, holder.comment);
                Resources.setText(PaginationViewNotifications.publicComment.getContent(), holder.comment);
            }, activity, new Object[]{data.getCommentId()}).sendToGetCommentById();
        }
    }

    private void postInDialog(Post post) throws JSONException {
        Dialog builder = postInDialog.getPostDialog(activity, post);
        String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());
        String token = Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString());

        if (login.equals(post.getAuthor())) {
            Button delete = postInDialog.getDelete();
            Button close = postInDialog.getClose();
            delete.setVisibility(View.VISIBLE);
            delete.setOnClickListener(v -> {
                Handler handler = new Handler();
                final int[] timer = {11};
                Runnable runnable = new Runnable() {
                    @SuppressLint("SetTextI18n")
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
                            Resources.setText(activity.getString(R.string.remove_post), delete);
                            if (postInDialog.getRunnable() != null)
                                postInDialog.getRunnable().run();
                        }
                        timer[0]--;
                        Resources.setText(timer[0] + " " + activity.getString(R.string.remove_post), delete);
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
                        Resources.setText(activity.getString(R.string.remove_post), delete);
                        if (postInDialog.getRunnable() != null) postInDialog.getRunnable().run();
                    });
                });
            }

        builder.show();
    }

    @Override
    public int getItemCount() {
        return notificationsLibrary.getNotifications().size();
    }

    public static class ViewHolderNotification extends RecyclerView.ViewHolder {
        private final LinearLayout notificationLayout;
        private final VideoView video;
        private final ImageView image;
        private final ImageView audio;
        private final ImageView userAvatar;
        private final TextView message;
        private final TextView comment;
        private final TextView date;

        public ViewHolderNotification(@NonNull View itemView) {
            super(itemView);
            notificationLayout = itemView.findViewById(R.id.notification_layout);
            userAvatar = itemView.findViewById(R.id.ava_view);
            message = itemView.findViewById(R.id.message);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
            video = itemView.findViewById(R.id.video_content);
            image = itemView.findViewById(R.id.image_content);
            audio = itemView.findViewById(R.id.audio_content);
        }
    }
}
