package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.cardview.widget.CardView;

import com.example.instagram.DAOs.Post;
import com.example.instagram.R;
import com.example.instagram.main_process.NewsLine;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class PostInDialog {
    private Activity activity;
    private ImageView postLike;
    private ImageView comments;
    private ImageView send;
    private ImageView save;
    private TextView qrLink;
    private ImageView qr;
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
    private CardView audioCard;
    private LinearLayout audioControllerLayout;
    private TextView timeLine;
    private SeekBar seekBar;
    private ImageView playStop;
    private ImageView playPrev;
    private ImageView playNext;
    private CardView cardViewQr;
    private LinearLayout buttons;
    private Button close;
    private Button delete;
    private Runnable runnable;

    public Runnable getRunnable() {
        return runnable;
    }

    public Button getDelete() {
        return delete;
    }

    public Button getClose() {
        return close;
    }

    public Dialog getPostDialog(Activity activity, Post post) throws JSONException {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(activity).inflate(R.layout.post, null, false);

        this.activity = activity;

        setViews(view);
        setContent(post);
        setListeners(post);

        Dialog dialog = GetDialog.getDialog(activity, view);

        runnable = () -> {
            if (audioController != null) audioController.clearHandler();
            dialog.dismiss();
        };

        close.setOnClickListener(v -> runnable.run());
        return dialog;
    }

    private void setViews(View view) {
        postLike = view.findViewById(R.id.post_like);
        comments = view.findViewById(R.id.comment);
        send = view.findViewById(R.id.send_post);
        save = view.findViewById(R.id.bookmark);

        cardViewQr = view.findViewById(R.id.card_qr);
        qrLink = view.findViewById(R.id.qr_link);
        qr = view.findViewById(R.id.qr);
        imageContent = view.findViewById(R.id.image_content);
        videoContent = view.findViewById(R.id.video_content);
        amountLikes = view.findViewById(R.id.amount_likes);
        authorLogin = view.findViewById(R.id.author_nickname);
        description = view.findViewById(R.id.description);
        taggedPeople = view.findViewById(R.id.tagged_people);
        taggedPeopleLayout = view.findViewById(R.id.tagged_people_layout);
        date = view.findViewById(R.id.hours);

        audioCard = view.findViewById(R.id.audio_card);
        audioControllerLayout = view.findViewById(R.id.audio_controller);
        timeLine = view.findViewById(R.id.time_line);
        seekBar = view.findViewById(R.id.seek_bar);
        playStop = view.findViewById(R.id.play_stop);
        playPrev = view.findViewById(R.id.play_prev);
        playNext = view.findViewById(R.id.play_next);
        buttons = view.findViewById(R.id.buttons_dialog);
        close = view.findViewById(R.id.close);
        delete = view.findViewById(R.id.remove);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ClickableViewAccessibility"})
    private void setContent(Post post) throws JSONException {
        String mime = post.getMimeType();
        String link = GetMediaLink.getMediaLink(activity, post.getResourceMedia());
        buttons.setVisibility(View.VISIBLE);

        String postId = Cache.loadStringSP(activity, post.getPostId());

        // region set media content
        if (mime.contains(activity.getString(R.string.mime_image))) {
            // set image
            SetImagesGlide.setImageGlide(activity, link, imageContent);
            imageContent.setVisibility(View.VISIBLE);
        } else if (mime.contains(activity.getString(R.string.mime_video))) {
            // set video
            Uri videoUri = Uri.parse(link);

            MediaController mediaController = new MediaController(activity);

            videoContent.setVideoURI(videoUri);
            mediaController.setAnchorView(videoContent);
            videoContent.setMediaController(mediaController);

            videoContent.start();
            videoContent.requestFocus();
            videoContent.setOnPreparedListener(mp -> mp.setLooping(true));
            videoContent.setVisibility(View.VISIBLE);
        } else if (mime.contains(activity.getString(R.string.mime_audio))) {
            // set audio
            Uri audioUri = Uri.parse(link);
            audioCard.setVisibility(View.VISIBLE);
            audioControllerLayout.setVisibility(View.VISIBLE);
            audioController = new AudioController(timeLine, seekBar, playStop, playPrev, playNext, activity, audioUri);
            audioController.initHandler(new Handler());
        }

        // region set other text info
        Resources.setText(post.getAuthor(), authorLogin);
        Resources.setText(post.getDescription(), description);

        description.setOnClickListener(v -> description.setMaxLines(description.getMaxLines() == 1 ? 100 : 1));

        Calendar calendar = DateFormatting.getCalendar(post.getDateOfAdd());
        Resources.setText(DateFormatting.formatDate(calendar.getTime()), date);
        // endregion

        // region set like, save
        if (!postId.equals("")) {
            boolean isLiked = Cache.loadBoolSP(activity, post.getPostId() + "." + "isLiked");
            Resources.setDrawableIntoImageView(activity.getResources().getDrawable(isLiked ? R.drawable.like_fill : R.drawable.like_empty, activity.getTheme()), postLike);
            post.setLiked(isLiked);

            int likes = Cache.loadIntSP(activity, post.getPostId() + "." + "likes");
            Resources.setText(Integer.toString(likes), amountLikes);
            post.setLikes(likes);
        } else {
            String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());
            new DoCallBack().setValues(null, activity, new Object[]{post.getPostId(), login, postLike, amountLikes, post}).sendToGetIsLikedForDialogPost();
        }

        if (!postId.equals("")) {
            boolean isSaved = Cache.loadBoolSP(activity, post.getPostId() + "." + "isSaved");
            Resources.setDrawableIntoImageView(activity.getResources().getDrawable(isSaved ? R.drawable.bookmark_saved : R.drawable.bookmark, activity.getTheme()), save);
            post.setSaved(isSaved);
        } else {
            String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());
            new DoCallBack().setValues(null, activity, new Object[]{post.getPostId(), login, save, post}).sendToGetIsSaved();
        }
        // endregion

        try {
            qr.setImageBitmap(QRGenerator.generateQR(post.getAuthor()));
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        qrLink.setOnClickListener(v -> {
            cardViewQr.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> Resources.setVisibility(View.GONE, cardViewQr), 10000L);
        });
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
                    new DoCallBack().setValues(null, activity, new Object[]{id, taggedPeopleLayout}).sendToGetTaggedPeople();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                // endregion
            }
        });

        postLike.setOnClickListener(v -> postSetStateLike(post));

        save.setOnClickListener(v -> {
            if (!post.isSaved()) {
                post.setSaved(true);
                Resources.setDrawableIntoImageView(activity.getResources().getDrawable(R.drawable.bookmark_saved, activity.getTheme()), save);
            } else {
                post.setSaved(false);
                Resources.setDrawableIntoImageView(activity.getResources().getDrawable(R.drawable.bookmark, activity.getTheme()), save);
            }

            Cache.saveSP(activity, post.getPostId() + "." + "isSaved", post.isSaved());

            TransitPost.postsToChangeFromOtherPage.removeIf(toRemove -> toRemove.getPostId().equals(post.getPostId()));
            TransitPost.postsToChangeFromOtherPage.add(post);

            String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());

            // region send save
            try {
                JSONObject jsonObject = Post.getSavedUnsaved(post.getPostId(), login, post.isSaved());
                jsonObject.put("token", Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString()));
                new DoCallBack().setValues(null, activity, new Object[]{jsonObject}).sendToSaveUnsavedPost();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // endregion
        });

        comments.setOnClickListener(v -> {
            NewsLine.mapPost = new Pair<>(null, post);
            Intent intent = Intents.getComments();
            if (intent != null) {
                activity.startActivity(intent);
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
            Resources.setDrawableIntoImageView(activity.getResources().getDrawable(R.drawable.like_fill, activity.getTheme()), postLike);
        } else {
            post.setLiked(false);
            post.setLikes(post.getLikes() - 1);
            Resources.setDrawableIntoImageView(activity.getResources().getDrawable(R.drawable.like_empty, activity.getTheme()), postLike);
        }

        Cache.saveSP(activity, post.getPostId() + "." + "likes", post.getLikes());
        Cache.saveSP(activity, post.getPostId() + "." + "isLiked", post.isLiked());

        TransitPost.postsToChangeFromOtherPage.forEach(postToFind -> {
            if (postToFind.getPostId().equals(post.getPostId()))
                postToFind.setLikes(post.getLikes());
        });

        Resources.setText(Integer.toString(post.getLikes()), amountLikes);
        String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());

        // region send unlike
        try {
            JSONObject jsonObject = Post.getLikedUnliked(post.getPostId(), login, post.isLiked());
            jsonObject.put("token", Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString()));
            // like unlike post
            new DoCallBack().setValues(null, activity, new Object[]{jsonObject}).sendToLikeUnlikePost();
        } catch (JSONException e) {
            Log.d("sendToLikeUnlikePost: (JSONException)", e.getMessage());
        }
        // endregion
    }
}
