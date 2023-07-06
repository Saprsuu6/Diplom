package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.AudioController;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.OnSwipeListener;
import com.example.instagram.services.Services;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPosts;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class PaginationViewPosts extends RecyclerView.Adapter<PaginationViewPosts.ViewHolderPosts> {
    private final Context context;
    private final Activity activity;
    private final PostsLibrary postsLibrary;
    private final PagingAdapterPosts pagingView;

    public PostsLibrary getPostsLibrary() {
        return postsLibrary;
    }

    public PaginationViewPosts(@Nullable Activity activity, Context context, PostsLibrary postsLibrary, PagingAdapterPosts pagingView) {
        this.activity = activity;
        this.context = context;
        this.postsLibrary = postsLibrary;
        this.pagingView = pagingView;
    }

    @NonNull
    @Override
    public PaginationViewPosts.ViewHolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post, parent, false); // don't forget to change
        return new ViewHolderPosts(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ResourceType", "ClickableViewAccessibility"})
    @Override
    public void onBindViewHolder(@NonNull PaginationViewPosts.ViewHolderPosts holder, int position) {
        Post data = postsLibrary.getDataArrayList().get(position);
        String mime = data.getMimeType();
        String mediaPath = Services.BASE_URL + context.getString(R.string.root_folder) + data.getResourceMedia();

        // region set media content
        if (mime.contains(context.getString(R.string.mime_image))) {
            // set image
            Glide.with(context).load(mediaPath).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.contentImg);
            holder.contentImg.setVisibility(View.VISIBLE);
        } else if (mime.contains(context.getString(R.string.mime_video))) {
            // set video
            Uri videoUri = Uri.parse(mediaPath);

            MediaController mediaController = new MediaController(context);

            holder.contentVideo.setVideoURI(videoUri);
            mediaController.setAnchorView(holder.contentVideo);
            holder.contentVideo.setMediaController(mediaController);

            holder.contentVideo.start();
            holder.contentVideo.requestFocus();
            holder.contentVideo.setVisibility(View.VISIBLE);
        } else if (mime.contains(context.getString(R.string.mime_audio))) {
            // set audio
            Uri audioUri = Uri.parse(mediaPath);
            holder.audioControllerLayout.setVisibility(View.VISIBLE);
            holder.audioController = new AudioController(holder.timeLine, holder.seekBar, holder.playStop, holder.playPrev, holder.playNext, context, audioUri);
            holder.audioController.initHandler(new Handler());
        }
        // endregion
        // region send request to get avatar
        try {
            new DoCallBack().setValues(null, context, new Object[]{data.getAuthor(), holder.avaView}).sendToGetAvaImage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // endregion
        // region set like, save
        if (postsLibrary.getDataArrayList().get(position).isLiked() == null) {
            String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
            try {
                new DoCallBack().setValues(null, context, new Object[]{data.getPostId(), login, holder.like_flag, holder.like, holder.amountLikes, data}).sendToGetIsLikedForDialogPost();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            holder.like_flag = postsLibrary.getDataArrayList().get(position).isLiked();
            holder.like.setImageDrawable(context.getResources().getDrawable(postsLibrary.getDataArrayList().get(position).isLiked() ? R.drawable.like_fill_gradient : R.drawable.like_empty_gradient, context.getTheme()));
            holder.amountLikes.setText(Integer.toString(data.getLikes()));
        }

        if (postsLibrary.getDataArrayList().get(position).isSaved() == null) {
            String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
            try {
                new DoCallBack().setValues(null, context, new Object[]{data.getPostId(), login, holder.bookmark_flag, holder.bookmark, data}).sendToGetIsSaved();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            holder.bookmark_flag = postsLibrary.getDataArrayList().get(position).isSaved();
            holder.bookmark.setImageDrawable(context.getResources().getDrawable(postsLibrary.getDataArrayList().get(position).isSaved() ? R.drawable.bookmark_saved : R.drawable.bookmark, context.getTheme()));
        }
        // endregion
        // region set other text info
        holder.login.setText(data.getAuthor());
        holder.taggedPeople.setText(context.getResources().getString(R.string.tagged_people));
        holder.amountLikesTitle.setText(R.string.amount_likes_title);
        holder.authorNickname.setText(data.getAuthor());
        holder.description.setText(data.getDescription());

        Calendar calendar = DateFormatting.getCalendar(data.getDateOfAdd());
        holder.hours.setText(DateFormatting.formatDate(calendar.getTime()));
        // endregion

        String token = Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString());
        try {
            JSONObject jsonObjectDelete = Post.getJSONToDeletePost(data.getPostId(), token);
            JSONObject jsonObjectLU = Post.getJSONToLikeUnlikePost(data.getPostId(), data.getAuthor(), holder.like_flag);
            jsonObjectLU.put("token", Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString()));
            // set swipe or double touch
            holder.media_content_layout.setOnTouchListener(new OnSwipeListener(context, holder.media_content_layout, new DoCallBack().setValues(pagingView::notifyAdapterToClearAll, context, new Object[]{jsonObjectDelete}), new DoCallBack().setValues(holder::likeUnlike, context, new Object[]{jsonObjectLU}), data.getAuthor()));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        holder.postLayout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_paging));
    }

    @Override
    public int getItemCount() {
        return postsLibrary.getDataArrayList().size();
    }

    public class ViewHolderPosts extends RecyclerView.ViewHolder {
        private final LinearLayout postLayout;
        private final ImageView avaView;
        private final LinearLayout taggedPeopleLayout;
        private final RelativeLayout media_content_layout;
        private final TextView taggedPeople;
        private final TextView login;
        private final ImageView contentImg;
        private final VideoView contentVideo;
        private final ImageView like;
        private final ImageView comment;
        private final ImageView send;
        private final ImageView postContext;
        private final TextView amountLikesTitle;
        private final ImageView bookmark;
        private final TextView amountLikes;
        private final TextView authorNickname;
        private final TextView description;
        private final TextView hours;
        // audio
        private AudioController audioController;
        private final LinearLayout audioControllerLayout;
        private final TextView timeLine;
        private final SeekBar seekBar;
        private final ImageView playStop;
        private final ImageView playPrev;
        private final ImageView playNext;
        boolean like_flag = false;
        boolean bookmark_flag = false;

        public ViewHolderPosts(@NonNull View itemView) {
            super(itemView);

            audioControllerLayout = itemView.findViewById(R.id.audio_controller);
            media_content_layout = itemView.findViewById(R.id.media_content_layout);
            timeLine = itemView.findViewById(R.id.time_line);
            seekBar = itemView.findViewById(R.id.seek_bar);
            playStop = itemView.findViewById(R.id.play_stop);
            playPrev = itemView.findViewById(R.id.play_prev);
            playNext = itemView.findViewById(R.id.play_next);

            postLayout = itemView.findViewById(R.id.post_layout);

            avaView = itemView.findViewById(R.id.post_author_page);
            login = itemView.findViewById(R.id.nick_view);

            postContext = itemView.findViewById(R.id.post_context);
            activity.registerForContextMenu(postContext); // post context registration

            contentImg = itemView.findViewById(R.id.image_content);
            contentVideo = itemView.findViewById(R.id.video_content);

            like = itemView.findViewById(R.id.post_like);
            comment = itemView.findViewById(R.id.comment);
            send = itemView.findViewById(R.id.send_post);
            bookmark = itemView.findViewById(R.id.bookmark);

            amountLikesTitle = itemView.findViewById(R.id.amount_likes_title);
            amountLikes = itemView.findViewById(R.id.amount_likes);
            authorNickname = itemView.findViewById(R.id.author_nickname);
            description = itemView.findViewById(R.id.description);

            taggedPeopleLayout = itemView.findViewById(R.id.tagged_people_layout);
            taggedPeople = itemView.findViewById(R.id.tagged_people);

            hours = itemView.findViewById(R.id.hours);

            setListeners();
        }

        @SuppressLint("ClickableViewAccessibility")
        private void setListeners() {
            contentVideo.setOnClickListener(v -> {
                if (contentVideo.isPlaying()) {
                    contentVideo.pause();
                } else {
                    contentVideo.start();
                }
            });

            postContext.setOnTouchListener((v, event) -> {
                NewsLine.mapPost = new Pair<>(getAdapterPosition(), postsLibrary.getDataArrayList().get(getAdapterPosition()));
                return false;
            });

            taggedPeople.setOnClickListener(v -> {
                String id = postsLibrary.getDataArrayList().get(getAdapterPosition()).getPostId();

                if (taggedPeopleLayout.getChildCount() > 0) taggedPeopleLayout.removeAllViews();
                // region get tagged people
                try {
                    new DoCallBack().setValues(null, context, new Object[]{id, taggedPeopleLayout}).sendToGetTaggedPeople();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // endregion
            });

            like.setOnClickListener(v -> likeUnlike());

            bookmark.setOnClickListener(v -> saveUnsaved());

            comment.setOnClickListener(v -> {
                NewsLine.mapPost = new Pair<>(getAdapterPosition(), postsLibrary.getDataArrayList().get(getAdapterPosition()));
                Intent intent = Intents.getComments();
                context.startActivity(intent);
            });

            send.setOnClickListener(v -> {
                // TODO: send
            });

            setSelfPages();
        }

        private void setSelfPages() {
            avaView.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{login.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            login.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{login.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            authorNickname.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{login.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
        public void likeUnlike() {
            int position = getAdapterPosition();
            if (!like_flag) {
                postsLibrary.getDataArrayList().get(position).setLikes(postsLibrary.getDataArrayList().get(position).getLikes() + 1);
                like.setImageDrawable(context.getResources().getDrawable(R.drawable.like_fill_gradient, context.getTheme()));
            } else {
                postsLibrary.getDataArrayList().get(position).setLikes(postsLibrary.getDataArrayList().get(position).getLikes() - 1);
                like.setImageDrawable(context.getResources().getDrawable(R.drawable.like_empty_gradient, context.getTheme()));
            }

            like_flag = !like_flag;
            postsLibrary.getDataArrayList().get(position).setLiked(like_flag);
            notifyDataSetChanged();

            String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());

            // region send unlike
            try {
                String postId = postsLibrary.getDataArrayList().get(position).getPostId();
                JSONObject jsonObject = Post.getJSONToLikeUnlikePost(postId, login, like_flag);
                jsonObject.put("token", Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString()));
                // like unlike post
                new DoCallBack().setValues(null, context, new Object[]{jsonObject}).likeUnlikePost();
            } catch (JSONException e) {
                Log.d("sendToLikeUnlikePost: (JSONException)", e.getMessage());
            }
            // endregion
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
        public void saveUnsaved() {
            int position = getAdapterPosition();
            if (!bookmark_flag) {
                bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_saved, context.getTheme()));
            } else {
                bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark, context.getTheme()));
            }

            bookmark_flag = !bookmark_flag;
            postsLibrary.getDataArrayList().get(position).setSaved(bookmark_flag);
            notifyDataSetChanged();

            String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
            String postId = postsLibrary.getDataArrayList().get(position).getPostId();

            // region send save
            try {
                JSONObject jsonObject = Post.getJSONToSaveUnsavedPost(postId, login, bookmark_flag);
                jsonObject.put("token", Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString()));
                new DoCallBack().setValues(null, context, new Object[]{jsonObject}).sendToSaveUnsavedPost();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // endregion
        }
    }
}
