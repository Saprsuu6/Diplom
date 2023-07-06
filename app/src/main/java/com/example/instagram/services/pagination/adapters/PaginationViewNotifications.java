package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Comment;
import com.example.instagram.DAOs.Notification;
import com.example.instagram.DAOs.NotificationsLibrary;
import com.example.instagram.DAOs.Post;
import com.example.instagram.R;
import com.example.instagram.main_process.UserPage;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.PostInDialog;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.pagination.paging_views.PagingAdapterNotifications;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class PaginationViewNotifications extends RecyclerView.Adapter<PaginationViewNotifications.ViewHolderNotification> {
    private final Context context;
    private final Activity activity;
    private final NotificationsLibrary notificationsLibrary;
    private final PagingAdapterNotifications pagingView;
    private final PostInDialog postInDialog = new PostInDialog();
    public static Post publicPost;

    public NotificationsLibrary getNotificationsLibrary() {
        return notificationsLibrary;
    }

    public PaginationViewNotifications(@Nullable Activity activity, Context context, NotificationsLibrary notificationsLibrary, PagingAdapterNotifications pagingView) {
        this.activity = activity;
        this.context = context;
        this.pagingView = pagingView;
        this.notificationsLibrary = notificationsLibrary;
    }

    @NonNull
    @Override
    public PaginationViewNotifications.ViewHolderNotification onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_notification, parent, false);
        return new PaginationViewNotifications.ViewHolderNotification(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolderNotification holder, int position) {
        Notification data = notificationsLibrary.getNotifications().get(position);

        // region send request to get avatar
        try {
            new DoCallBack().setValues(null, context, new Object[]{data.getUser(), holder.userAvatar}).sendToGetAvaImage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // endregion

        try {
            setResources(holder, data);
        } catch (JSONException | ParseException e) {
            throw new RuntimeException(e);
        }

        holder.date.setText(DateFormatting.formatDate(data.getDate()));
        holder.notificationLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_paging));
    }

    private void setResources(@NonNull ViewHolderNotification holder, Notification data) throws JSONException, ParseException {
        holder.messagePre.setText(context.getResources().getString(R.string.user_notification));
        holder.messageUser.setText(data.getUser());

        switch (data.getAction()) {
            case "subscribe":
                holder.messagePost.setText(context.getResources().getString(R.string.subscribe_notification));
                break;
            case "unsubscribe":
                holder.messagePost.setText(context.getResources().getString(R.string.unsubscribe_notification));
                break;
            case "like":
                holder.messagePost.setText(context.getResources().getString(R.string.like_notification));
                setMediaContent(holder, data);
                break;
            case "save":
                holder.messagePost.setText(context.getResources().getString(R.string.save_notification));
                setMediaContent(holder, data);
                break;
            case "tage":
                holder.messagePost.setText(context.getResources().getString(R.string.tag_notification));
                setMediaContent(holder, data);
                break;
            case "comment":
                holder.messagePost.setText(context.getResources().getString(R.string.comment_notification));
                setMediaContent(holder, data);
                break;
            case "response":
                holder.messagePost.setText(context.getResources().getString(R.string.reply_notification));
                setCommentContent(holder, data);
                break;
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setMediaContent(@NonNull ViewHolderNotification holder, Notification data) throws JSONException {
        if (data.getPostId() != null) {
            new DoCallBack().setValues(() -> {
                String mime = PaginationViewNotifications.publicPost.getMimeType();
                String mediaPath = Services.BASE_URL + context.getString(R.string.root_folder) + PaginationViewNotifications.publicPost.getResourceMedia();

                // region set media content
                if (mime.contains(context.getString(R.string.mime_image))) {
                    //set image
                    holder.image.setVisibility(View.VISIBLE);
                    Glide.with(context).load(mediaPath).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.image);

                    try {
                        holder.image.setOnClickListener(postInDialog(PaginationViewNotifications.publicPost));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else if (mime.contains(context.getString(R.string.mime_video))) {
                    // set video
                    holder.postVideo.setVisibility(View.VISIBLE);
                    Uri videoUri = Uri.parse(mediaPath);
                    holder.video.setVideoURI(videoUri);

                    try {
                        holder.postVideo.setOnClickListener(postInDialog(PaginationViewNotifications.publicPost));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                } else if (mime.contains(context.getString(R.string.mime_audio))) {
                    // set audio
                    holder.audio.setVisibility(View.VISIBLE);
                    holder.audio.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    holder.audio.setImageDrawable(context.getDrawable(R.drawable.play_cell));

                    try {
                        holder.audio.setOnClickListener(postInDialog(PaginationViewNotifications.publicPost));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
            }, context, new Object[]{data.getPostId()}).sendToGetPostById();
        }
    }

    private void setCommentContent(@NonNull ViewHolderNotification holder, Notification data) throws JSONException {
        if (data.getCommentId() != null) {
            Comment comment = new Comment();
            new DoCallBack().setValues(() -> {
                holder.comment.setText(comment.getContent());
                // TODO move to comments on this comment
            }, context, new Object[]{data.getCommentId(), comment}).sendToGetCommentById();
        }
    }

    private View.OnClickListener postInDialog(Post post) throws JSONException {
        AlertDialog.Builder builder = postInDialog.getPostDialog(context, post, pagingView);
        String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
        String token = Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString());

        if (UserPage.userPage != null) {
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
        }

        return v -> builder.show();
    }

    @Override
    public int getItemCount() {
        return notificationsLibrary.getNotifications().size();
    }

    public class ViewHolderNotification extends RecyclerView.ViewHolder {
        private final LinearLayout notificationLayout;
        private final RelativeLayout postVideo;
        private final VideoView video;
        private final ImageView image;
        private final ImageView audio;
        private final ImageView userAvatar;
        private final TextView messagePre;
        private final TextView comment;
        private final TextView messageUser;
        private final TextView messagePost;
        private final TextView date;

        public ViewHolderNotification(@NonNull View itemView) {
            super(itemView);
            notificationLayout = itemView.findViewById(R.id.notification_layout);
            userAvatar = itemView.findViewById(R.id.ava_view);
            messagePre = itemView.findViewById(R.id.message_pre);
            messageUser = itemView.findViewById(R.id.message_user);
            messagePost = itemView.findViewById(R.id.message_post);
            comment = itemView.findViewById(R.id.comment);
            date = itemView.findViewById(R.id.date);
            postVideo = itemView.findViewById(R.id.video_layout);
            video = itemView.findViewById(R.id.video_content);
            image = itemView.findViewById(R.id.image_content);
            audio = itemView.findViewById(R.id.audio_content);

            setListeners();
        }

        private void setListeners() {
            messageUser.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{messageUser.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            userAvatar.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{messageUser.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
