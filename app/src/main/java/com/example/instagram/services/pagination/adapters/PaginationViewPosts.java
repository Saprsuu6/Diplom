package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.AudioController;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.GetMediaLink;
import com.example.instagram.services.Intents;
import com.example.instagram.services.OnSwipeListener;
import com.example.instagram.services.QRGenerator;
import com.example.instagram.services.Resources;
import com.example.instagram.services.SetImagesGlide;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPosts;
import com.google.zxing.WriterException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class PaginationViewPosts extends RecyclerView.Adapter<PaginationViewPosts.ViewHolderPosts> {
    private final Activity activity;
    private final PostsLibrary postsLibrary;
    private final PagingAdapterPosts pagingView;

    public PostsLibrary getPostsLibrary() {
        return postsLibrary;
    }

    public PaginationViewPosts(Activity activity, PostsLibrary postsLibrary, PagingAdapterPosts pagingView) {
        this.activity = activity;
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
        holder.post = data;
        String mime = data.getMimeType();
        String link = GetMediaLink.getMediaLink(activity, data.getResourceMedia());

        // cache
        String postId = Cache.loadStringSP(activity, data.getPostId());
        Cache.saveSP(activity, data.getPostId(), data.getPostId());

        // region set media content
        if (mime.contains(activity.getString(R.string.mime_image))) {
            // set image
            Resources.setVisibility(View.VISIBLE, holder.contentImg);
            SetImagesGlide.setImageGlide(activity, link, holder.contentImg);
        } else if (mime.contains(activity.getString(R.string.mime_video))) {
            // set video
            Uri videoUri = Uri.parse(link);
            Resources.setVisibility(View.VISIBLE, holder.contentVideo);
            holder.contentVideo.setVideoURI(videoUri);
            holder.contentVideo.setOnPreparedListener(mp -> mp.setLooping(true));
            holder.contentVideo.start();
            holder.contentVideo.requestFocus();
        } else if (mime.contains(activity.getString(R.string.mime_audio))) {
            // set audio
            Uri audioUri = Uri.parse(link);
            Resources.setVisibility(View.VISIBLE, holder.audioCard);
            Resources.setVisibility(View.VISIBLE, holder.audioControllerLayout);
            holder.audioController = new AudioController(holder.timeLine, holder.seekBar, holder.playStop, holder.playPrev, holder.playNext, activity, audioUri);
            holder.audioController.initHandler(new Handler());
        }
        // endregion
        // region send request to get avatar
        if (!postId.equals("")) {
            String authorAvatarFromCache = Cache.loadStringSP(activity, data.getPostId() + "." + "authorAvatar");
            SetImagesGlide.setImageGlide(activity, authorAvatarFromCache, holder.avaView);
        } else {
            try {
                new DoCallBack().setValues(null, activity, new Object[]{data.getAuthor(), holder.avaView, data.getPostId()}).sendToGetAvaImage();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            Cache.saveSP(activity, data.getPostId() + "." + "authorAvatar", link);
        }
        // endregion
        // region set like, save
        String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());

        if (!postId.equals("")) {
            boolean isLiked = Cache.loadBoolSP(activity, data.getPostId() + "." + "isLiked");
            Resources.setDrawableIntoImageView(activity.getResources().getDrawable(isLiked ? R.drawable.like_fill : R.drawable.like_empty, activity.getTheme()), holder.like);
            holder.post.setLiked(isLiked);

            int likes = Cache.loadIntSP(activity, data.getPostId() + "." + "likes");
            Resources.setText(Integer.toString(likes), holder.amountLikes);
            holder.post.setLikes(likes);
        } else {
            try {
                new DoCallBack().setValues(() -> {
                    String token = Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString());
                    try {
                        JSONObject jsonObjectDelete = Post.getJSONToDeletePost(data.getPostId(), token);
                        JSONObject jsonObjectLU = Post.getJSONToLikeUnlikePost(data.getPostId(), data.getAuthor(), holder.post.isLiked());
                        jsonObjectLU.put("token", Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString()));
                        // set swipe or double touch
                        holder.mediaContentLayout.setOnTouchListener(new OnSwipeListener(activity, holder.mediaContentLayout, new DoCallBack().setValues(() -> pagingView.notifyAdapterToClearByPosition(position), activity, new Object[]{jsonObjectDelete}), new DoCallBack().setValues(holder::likeUnlike, activity, new Object[]{jsonObjectLU}), data.getAuthor()));
                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }, activity, new Object[]{data.getPostId(), login, holder.like, holder.amountLikes, holder.post}).sendToGetIsLikedForDialogPost();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }


        if (!postId.equals("")) {
            boolean isSaved = Cache.loadBoolSP(activity, data.getPostId() + "." + "isSaved");
            Resources.setDrawableIntoImageView(activity.getResources().getDrawable(isSaved ? R.drawable.bookmark_saved : R.drawable.bookmark, activity.getTheme()), holder.bookmark);
            holder.post.setSaved(isSaved);
        } else {
            try {
                new DoCallBack().setValues(null, activity, new Object[]{data.getPostId(), login, holder.bookmark, holder.post}).sendToGetIsSaved();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        // endregion
        // region set other text info
        Resources.setText(data.getAuthor(), holder.login);
        Resources.setText(data.getAuthor(), holder.authorNickname);
        Resources.setText(data.getDescription(), holder.description);

        try {
            holder.qr.setImageBitmap(QRGenerator.generateQR(data.getAuthor()));
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        holder.qrLink.setOnClickListener(v -> {
            Resources.setVisibility(View.VISIBLE, holder.cardViewQr);
            new Handler().postDelayed(() -> holder.cardViewQr.setVisibility(View.GONE), 10000L);
        });

        holder.description.setOnClickListener(v -> holder.description.setMaxLines(holder.description.getMaxLines() == 1 ? 100 : 1));

        Calendar calendar = DateFormatting.getCalendar(data.getDateOfAdd());
        Resources.setText(DateFormatting.formatDate(calendar.getTime()), holder.hours);
        // endregion

        Resources.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.anim_paging), holder.postLayout);
    }

    @Override
    public int getItemCount() {
        return postsLibrary.getDataArrayList().size();
    }

    public class ViewHolderPosts extends RecyclerView.ViewHolder {
        private final LinearLayout postLayout;
        private final ImageView avaView;
        private final LinearLayout taggedPeopleLayout;
        private final RelativeLayout mediaContentLayout;
        private final TextView taggedPeople;
        private final TextView qrLink;
        private final TextView login;
        private final ImageView contentImg;
        private final VideoView contentVideo;
        private final ImageView like;
        private final ImageView comment;
        private final ImageView send;
        private final ImageView postContext;
        private final ImageView bookmark;
        private final TextView amountLikes;
        private final TextView authorNickname;
        private final TextView description;
        private final TextView hours;
        private final CardView cardViewQr;
        private final ImageView qr;
        // audio
        private AudioController audioController;
        private final CardView audioCard;
        private final LinearLayout audioControllerLayout;
        private final TextView timeLine;
        private final SeekBar seekBar;
        private final ImageView playStop;
        private final ImageView playPrev;
        private final ImageView playNext;
        private Post post;

        public ViewHolderPosts(@NonNull View itemView) {
            super(itemView);

            audioCard = itemView.findViewById(R.id.audio_card);
            audioControllerLayout = itemView.findViewById(R.id.audio_controller);
            mediaContentLayout = itemView.findViewById(R.id.media_content_layout);
            timeLine = itemView.findViewById(R.id.time_line);
            seekBar = itemView.findViewById(R.id.seek_bar);
            playStop = itemView.findViewById(R.id.play_stop);
            playPrev = itemView.findViewById(R.id.play_prev);
            playNext = itemView.findViewById(R.id.play_next);
            qrLink = itemView.findViewById(R.id.qr_link);
            cardViewQr = itemView.findViewById(R.id.card_qr);
            qr = itemView.findViewById(R.id.qr);
            postLayout = itemView.findViewById(R.id.post_layout);

            avaView = itemView.findViewById(R.id.post_author_page);
            login = itemView.findViewById(R.id.nick_view);

            postContext = itemView.findViewById(R.id.post_context);
            activity.registerForContextMenu(postContext); // post activity registration

            contentImg = itemView.findViewById(R.id.image_content);
            contentVideo = itemView.findViewById(R.id.video_content);

            like = itemView.findViewById(R.id.post_like);
            comment = itemView.findViewById(R.id.comment);
            send = itemView.findViewById(R.id.send_post);
            bookmark = itemView.findViewById(R.id.bookmark);

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
                    new DoCallBack().setValues(null, activity, new Object[]{id, taggedPeopleLayout}).sendToGetTaggedPeople();
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
                activity.startActivity(intent);
            });

            send.setOnClickListener(v -> {
                // TODO: send
            });

            setSelfPages();
        }

        private void setSelfPages() {
            avaView.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{login.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            login.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{login.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            authorNickname.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{login.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }

        @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
        public void likeUnlike() {
            int position = getAdapterPosition();
            if (!post.isLiked()) {
                post.setLiked(true);
                postsLibrary.getDataArrayList().get(position).setLikes(postsLibrary.getDataArrayList().get(position).getLikes() + 1);
                Resources.setDrawableIntoImageView(activity.getResources().getDrawable(R.drawable.like_fill, activity.getTheme()), like);
            } else {
                post.setLiked(false);
                postsLibrary.getDataArrayList().get(position).setLikes(postsLibrary.getDataArrayList().get(position).getLikes() - 1);
                Resources.setDrawableIntoImageView(activity.getResources().getDrawable(R.drawable.like_empty, activity.getTheme()), like);
            }

            postsLibrary.getDataArrayList().get(position).setLiked(post.isLiked());
            notifyItemChanged(position);

            String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());

            // region send unlike
            try {
                String postId = postsLibrary.getDataArrayList().get(position).getPostId();
                JSONObject jsonObject = Post.getJSONToLikeUnlikePost(postId, login, post.isLiked());
                jsonObject.put("token", Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString()));
                // like unlike post
                new DoCallBack().setValues(null, activity, new Object[]{jsonObject}).sendToLikeUnlikePost();
            } catch (JSONException e) {
                Log.d("sendToLikeUnlikePost: (JSONException)", e.getMessage());
            }
            // endregion
        }

        @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
        public void saveUnsaved() {
            int position = getAdapterPosition();
            if (!post.isSaved()) {
                post.setSaved(true);
                Resources.setDrawableIntoImageView(activity.getResources().getDrawable(R.drawable.bookmark_saved, activity.getTheme()), bookmark);
            } else {
                post.setSaved(false);
                Resources.setDrawableIntoImageView(activity.getResources().getDrawable(R.drawable.bookmark, activity.getTheme()), bookmark);
            }

            postsLibrary.getDataArrayList().get(position).setSaved(post.isSaved());
            notifyItemChanged(position);

            String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());
            String postId = postsLibrary.getDataArrayList().get(position).getPostId();

            // region send save
            try {
                JSONObject jsonObject = Post.getJSONToSaveUnsavedPost(postId, login, post.isSaved());
                jsonObject.put("token", Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString()));
                new DoCallBack().setValues(null, activity, new Object[]{jsonObject}).sendToSaveUnsavedPost();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            // endregion
        }
    }
}
