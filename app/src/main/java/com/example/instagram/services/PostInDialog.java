package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.R;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPostsInCells;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostInDialog {
    private Context context;
    private ImageView postLike;
    private ImageView comments;
    private ImageView send;
    private ImageView save;
    private ImageView imageContent;
    private TextView amountLikesTitle;
    private TextView amountLikes;
    private TextView authorLogin;
    private TextView description;
    private TextView taggedPeople;
    private LinearLayout taggedPeopleLayout;
    private TextView date;
    boolean like_flag = false;
    boolean bookmark_flag = false;

    public AlertDialog.Builder getPostDialog(Context context, Post post, PagingViewGetAllPostsInCells pagingView) {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.post, null, false);

        this.context = context;

        postLike = view.findViewById(R.id.post_like);
        comments = view.findViewById(R.id.comment);
        send = view.findViewById(R.id.send_post);
        save = view.findViewById(R.id.bookmark);

        imageContent = view.findViewById(R.id.image_content);
        amountLikesTitle = view.findViewById(R.id.amount_likes_title);
        amountLikes = view.findViewById(R.id.amount_likes);
        authorLogin = view.findViewById(R.id.author_nickname);
        description = view.findViewById(R.id.description);
        taggedPeople = view.findViewById(R.id.tagged_people);
        taggedPeopleLayout = view.findViewById(R.id.tagged_people_layout);
        date = view.findViewById(R.id.hours);

        setContent(post);
        setListeners(post);

        return new AlertDialog.Builder(context).setCancelable(false).setView(view).setPositiveButton(context.getApplicationContext().getString(R.string.permission_ok), null).setNegativeButton(context.getApplicationContext().getString(R.string.remove_post), (dialog, which) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context).setMessage(context.getApplicationContext().getString(R.string.remove_post_question)).setPositiveButton(context.getApplicationContext().getString(R.string.yes), (dialog1, which1) -> {
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
            builder.show();
        });
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void setContent(Post post) {
        if (post.getResourceImg().equals("")) {
            // set image
            Glide.with(context).load(Services.BASE_URL + post.getResourceVideo()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageContent);
        } else {
            // set video
            Glide.with(context).load(Services.BASE_URL + post.getResourceImg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageContent);
        }

        // region set other text info
        taggedPeople.setText(context.getResources().getString(R.string.tagged_people));
        amountLikesTitle.setText(R.string.amount_likes_title);
        authorLogin.setText(post.getAuthor());
        description.setText(post.getDescription());

        Calendar calendar = DateFormatting.getCalendar(post.getDateOfAdd());
        date.setText(DateFormatting.formatDate(calendar.getTime()));
        // endregion

        // region set like, save
        if (post.isLiked() == null) {
            try {
                Services.sendToGetIsLiked(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                JSONObject object = new JSONObject(response.body());

                                // set liked
                                like_flag = object.getBoolean("isLiked");
                                postLike.setImageDrawable(context.getResources().getDrawable(like_flag ? R.drawable.like_fill_gradient : R.drawable.like_empty_gradient, context.getTheme()));
                                post.setLiked(like_flag);

                                // set amount likes
                                int likes = object.getInt("amountLikes");
                                amountLikes.setText(Integer.toString(likes));
                                post.setLikes(likes);
                            } catch (JSONException e) {
                                Log.d("sendToGetIsLiked: (onResponse)", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToGetIsLiked: (onFailure)", t.getMessage());
                    }
                }, post.getPostId(), TransitUser.user.getLogin());
            } catch (JSONException e) {
                Log.d("sendToGetIsLiked: (JSONException)", e.getMessage());
            }
        } else {
            like_flag = post.isLiked();
            postLike.setImageDrawable(context.getResources().getDrawable(post.isLiked() ? R.drawable.like_fill_gradient : R.drawable.like_empty_gradient, context.getTheme()));
            amountLikes.setText(Integer.toString(post.getLikes()));
        }

        if (post.isSaved() == null) {
            try {
                Services.sendToGetIsSaved(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                JSONObject object = new JSONObject(response.body());

                                // set saved
                                bookmark_flag = object.getBoolean("isSaved");
                                save.setImageDrawable(context.getResources().getDrawable(bookmark_flag ? R.drawable.bookmark_saved : R.drawable.bookmark, context.getTheme()));
                                post.setSaved(bookmark_flag);
                            } catch (JSONException e) {
                                Log.d("sendToGetIsLiked: (onResponse)", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToGetIsLiked: (onFailure)", t.getMessage());
                    }
                }, post.getPostId(), TransitUser.user.getLogin());
            } catch (JSONException e) {
                Log.d("sendToGetIsLiked: (JSONException)", e.getMessage());
            }
        } else {
            bookmark_flag = post.isSaved();
            save.setImageDrawable(context.getResources().getDrawable(post.isSaved() ? R.drawable.bookmark_saved : R.drawable.bookmark, context.getTheme()));
        }
        // endregion
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    private void setListeners(Post post) {
        taggedPeople.setOnClickListener(v -> {
            if (taggedPeopleLayout.getChildCount() > 1) {
                taggedPeopleLayout.setVisibility(taggedPeopleLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
            } else {
                String id = post.getPostId();
                // region get tagged people
                try {
                    Services.sendToGetTaggedPeople(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.code() == 200) {
                                try {
                                    assert response.body() != null;
                                    JSONObject logins = new JSONObject(response.body());

                                    for (int i = 0; i < logins.length(); i++) {
                                        String login = logins.getString(Integer.toString(i));
                                        TextView taggedPerson = new TextView(context);
                                        taggedPerson.setTextSize(18);
                                        taggedPerson.setText(login);

                                        taggedPeopleLayout.addView(taggedPerson);
                                    }
                                } catch (JSONException e) {
                                    Log.d("JSONException: (sendToGetTaggedPeople)", e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("JSONException: (sendToGetTaggedPeople)", t.getMessage());
                        }
                    }, id);
                } catch (JSONException e) {
                    Log.d("JSONException: (sendToGetTaggedPeople)", e.getMessage());
                }
                // endregion
            }
        });

        postLike.setOnClickListener(v -> {
            if (!like_flag) {
                post.setLikes(post.getLikes() + 1);
                postLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like_fill_gradient, context.getTheme()));
            } else {
                post.setLikes(post.getLikes() - 1);
                postLike.setImageDrawable(context.getResources().getDrawable(R.drawable.like_empty_gradient, context.getTheme()));
            }

            TransitPost.postsToChangeFromOtherPage.removeIf(toRemove -> toRemove.getPostId().equals(post.getPostId()));
            TransitPost.postsToChangeFromOtherPage.add(post);

            like_flag = !like_flag;
            post.setLiked(like_flag);
            amountLikes.setText(Integer.toString(post.getLikes()));

            // region send unlike
            try {
                JSONObject object = new JSONObject();
                object.put("postId", post.getPostId());
                object.put("login", TransitUser.user.getLogin());
                object.put("isLiked", like_flag);

                Services.sendToLikeUnlikePost(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Log.d("sendToLikeUnlikePost: (onResponse)", response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToLikeUnlikePost: (onFailure)", t.getMessage());
                    }
                }, object.toString());
            } catch (JSONException e) {
                Log.d("sendToLikeUnlikePost: (JSONException)", e.getMessage());
            }
            // endregion
        });

        save.setOnClickListener(v -> {
            if (!bookmark_flag) {
                save.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_saved, context.getTheme()));
            } else {
                save.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark, context.getTheme()));
            }

            TransitPost.postsToChangeFromOtherPage.removeIf(toRemove -> toRemove.getPostId().equals(post.getPostId()));
            TransitPost.postsToChangeFromOtherPage.add(post);

            bookmark_flag = !bookmark_flag;
            post.setSaved(bookmark_flag);

            // region send save
            try {
                JSONObject object = new JSONObject();
                object.put("postId", post.getPostId());
                object.put("login", TransitUser.user.getLogin());
                object.put("isSaved", bookmark_flag);

                Services.sendToSaveUnsavedPost(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        Log.d("sendToSaveUnsavedPost: (onResponse)", response.body());
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToSaveUnsavedPost: (onFailure)", t.getMessage());
                    }
                }, object.toString());
            } catch (JSONException e) {
                Log.d("sendToSaveUnsavedPost: (JSONException)", e.getMessage());
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
}
