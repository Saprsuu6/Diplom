package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.R;
import com.example.instagram.main_process.NewsLine;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class PostInDialog {
    private Context context;
    private ImageView postLike;
    private ImageView comments;
    private ImageView send;
    private ImageView save;
    private ImageView imageContent;
    private VideoView videoContent;
    private TextView amountLikes;
    private TextView authorLogin;
    private TextView description;
    private TextView taggedPeople;
    private LinearLayout taggedPeopleLayout;
    private TextView date;
    // audio
    private AudioController audioController;
    private LinearLayout audioControllerLayout;
    private TextView timeLine;
    private SeekBar seekBar;
    private ImageView playStop;
    private ImageView playPrev;
    private ImageView playNext;

    public AlertDialog.Builder getPostDialog(Context context, Post post) throws JSONException {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.post, null, false);

        this.context = context;

        setViews(view);
        setContent(post);
        setListeners(post);

        return new AlertDialog.Builder(context).setCancelable(false).setView(view).setPositiveButton(context.getApplicationContext().getString(R.string.permission_ok), (dialog, which) -> {
            if (audioController != null) audioController.clearHandler();
        });
    }

    private void setViews(View view) {
        postLike = view.findViewById(R.id.post_like);
        comments = view.findViewById(R.id.comment);
        send = view.findViewById(R.id.send_post);
        save = view.findViewById(R.id.bookmark);

        imageContent = view.findViewById(R.id.image_content);
        videoContent = view.findViewById(R.id.video_content);
        amountLikes = view.findViewById(R.id.amount_likes);
        authorLogin = view.findViewById(R.id.author_nickname);
        description = view.findViewById(R.id.description);
        taggedPeople = view.findViewById(R.id.tagged_people);
        taggedPeopleLayout = view.findViewById(R.id.tagged_people_layout);
        date = view.findViewById(R.id.hours);

        audioControllerLayout = view.findViewById(R.id.audio_controller);
        timeLine = view.findViewById(R.id.time_line);
        seekBar = view.findViewById(R.id.seek_bar);
        playStop = view.findViewById(R.id.play_stop);
        playPrev = view.findViewById(R.id.play_prev);
        playNext = view.findViewById(R.id.play_next);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void setContent(Post post) throws JSONException {
        String mime = post.getMimeType();
        String mediaPath = Services.BASE_URL + context.getString(R.string.root_folder) + post.getResourceMedia();

        // region set media content
        if (mime.contains(context.getString(R.string.mime_image))) {
            // set image
            Glide.with(context).load(mediaPath).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageContent);
            imageContent.setVisibility(View.VISIBLE);
        } else if (mime.contains(context.getString(R.string.mime_video))) {
            // set video
            Uri videoUri = Uri.parse(mediaPath);

            MediaController mediaController = new MediaController(context);

            videoContent.setVideoURI(videoUri);
            mediaController.setAnchorView(videoContent);
            videoContent.setMediaController(mediaController);

            videoContent.start();
            videoContent.requestFocus();
            videoContent.setOnPreparedListener(mp -> mp.setLooping(true));
            videoContent.setVisibility(View.VISIBLE);
        } else if (mime.contains(context.getString(R.string.mime_audio))) {
            // set audio
            Uri audioUri = Uri.parse(mediaPath);
            audioControllerLayout.setVisibility(View.VISIBLE);
            audioController = new AudioController(timeLine, seekBar, playStop, playPrev, playNext, context, audioUri);
            audioController.initHandler(new Handler());
        }

        // region set other text info
        authorLogin.setText(post.getAuthor());
        description.setText(post.getDescription());

        description.setOnClickListener(v -> {
            description.setMaxLines(description.getMaxLines() == 1 ? 100 : 1);
        });

        Calendar calendar = DateFormatting.getCalendar(post.getDateOfAdd());
        date.setText(DateFormatting.formatDate(calendar.getTime()));
        // endregion

        // region set like, save
        if (post.isLiked() == null) {
            String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
            new DoCallBack().setValues(null, context, new Object[]{post.getPostId(), login, postLike, amountLikes, post}).sendToGetIsLikedForDialogPost();
        } else {
            postLike.setImageDrawable(context.getResources().getDrawable(post.isLiked() ? R.drawable.like_fill_gradient : R.drawable.like_empty_gradient, context.getTheme()));
            amountLikes.setText(Integer.toString(post.getLikes()));
        }

        if (post.isSaved() == null) {
            String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
            new DoCallBack().setValues(null, context, new Object[]{post.getPostId(), login, save, post}).sendToGetIsSaved();
        } else {
            save.setImageDrawable(context.getResources().getDrawable(post.isSaved() ? R.drawable.bookmark_saved : R.drawable.bookmark, context.getTheme()));
        }
        // endregion
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void setListeners(Post post) {
        videoContent.setOnClickListener(v -> {
            if (videoContent.isPlaying()) {
                videoContent.pause();
            } else {
                videoContent.start();
            }
        });

        taggedPeople.setOnClickListener(v -> {
            if (taggedPeopleLayout.getChildCount() > 1) {
                taggedPeopleLayout.setVisibility(taggedPeopleLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            } else {
                String id = post.getPostId();
                // region get tagged people
                try {
                    new DoCallBack().setValues(null, context, new Object[]{id, taggedPeopleLayout}).sendToGetTaggedPeople();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // endregion
            }
        });

        postLike.setOnClickListener(v -> {
            postSetStateLike(post);
        });

        save.setOnClickListener(v -> {
            if (!post.isSaved()) {
                post.setSaved(true);
                save.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_saved, context.getTheme()));
            } else {
                post.setSaved(false);
                save.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark, context.getTheme()));
            }

            TransitPost.postsToChangeFromOtherPage.removeIf(toRemove -> toRemove.getPostId().equals(post.getPostId()));
            TransitPost.postsToChangeFromOtherPage.add(post);

            String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());

            // region send save
            try {
                JSONObject jsonObject = Post.getSavedUnsaved(post.getPostId(), login, post.isSaved());
                jsonObject.put("token", Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString()));
                new DoCallBack().setValues(null, context, new Object[]{jsonObject}).sendToSaveUnsavedPost();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // endregion
        });

        comments.setOnClickListener(v -> {
            NewsLine.mapPost = new Pair<>(null, post);
            Intent intent = Intents.getComments();
            if (intent != null) {
                context.startActivity(intent);
            }
        });

        send.setOnClickListener(v -> {
            // TODO: send
        });
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void postSetStateLike(Post post) {
        if (!post.isLiked()) {
            post.setLiked(true);
            post.setLikes(post.getLikes() + 1);
            postLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like_fill_gradient, context.getTheme()));
        } else {
            post.setLiked(false);
            post.setLikes(post.getLikes() - 1);
            postLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like_empty_gradient, context.getTheme()));
        }

        TransitPost.postsToChangeFromOtherPage.forEach(postToFind -> {
            if (postToFind.getPostId().equals(post.getPostId()))
                postToFind.setLikes(post.getLikes());
        });

        amountLikes.setText(Integer.toString(post.getLikes()));

        String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());

        // region send unlike
        try {
            JSONObject jsonObject = Post.getLikedUnliked(post.getPostId(), login, post.isLiked());
            jsonObject.put("token", Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString()));
            // like unlike post
            new DoCallBack().setValues(null, context, new Object[]{jsonObject}).likeUnlikePost();
        } catch (JSONException e) {
            Log.d("sendToLikeUnlikePost: (JSONException)", e.getMessage());
        }
        // endregion
    }
}
