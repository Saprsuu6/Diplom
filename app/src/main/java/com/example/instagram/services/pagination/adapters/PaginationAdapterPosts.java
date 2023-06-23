package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaginationAdapterPosts extends RecyclerView.Adapter<PaginationAdapterPosts.ViewHolderPosts> {
    private final Context context;
    private final Activity activity;
    private final PostsLibrary postsLibrary;

    public PostsLibrary getPostsLibrary() {
        return postsLibrary;
    }

    public PaginationAdapterPosts(@Nullable Activity activity, Context context, PostsLibrary postsLibrary) {
        this.activity = activity;
        this.context = context;
        this.postsLibrary = postsLibrary;
    }

    @NonNull
    @Override
    public PaginationAdapterPosts.ViewHolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_post, parent, false); // don't forget to change
        return new ViewHolderPosts(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n", "ResourceType"})
    @Override
    public void onBindViewHolder(@NonNull PaginationAdapterPosts.ViewHolderPosts holder, int position) {
        Post data = postsLibrary.getDataArrayList().get(position);

        // region set media content
        if (data.getResourceImg().equals("")) {
            // set image
            Glide.with(context).load(Services.BASE_URL + data.getResourceVideo()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.content);
        } else {
            Glide.with(context).load(Services.BASE_URL + data.getResourceImg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.content);
        }
        // endregion
        // region send request to get avatar
        try {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avaLink = response.body();

                        // set ava
                        Glide.with(context).load(Services.BASE_URL + avaLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.avaView);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, data.getAuthor());
        } catch (JSONException e) {
            Log.d("sendToGetAva: (JSONException)", e.getMessage());
        }
        // endregion
        // region set like, save
        if (postsLibrary.getDataArrayList().get(position).isLiked() == null) {
            try {
                Services.sendToGetIsLiked(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                JSONObject object = new JSONObject(response.body());

                                // set liked
                                holder.like_flag = object.getBoolean("isLiked");
                                holder.like.setImageDrawable(context.getResources().getDrawable(holder.like_flag ? R.drawable.like_fill_gradient : R.drawable.like_empty_gradient, context.getTheme()));
                                data.setLiked(holder.like_flag);

                                // set amount likes
                                int amountLikes = object.getInt("amountLikes");
                                holder.amountLikes.setText(Integer.toString(amountLikes));
                                data.setLikes(amountLikes);
                            } catch (JSONException e) {
                                Log.d("sendToGetIsLiked: (onResponse)", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToGetIsLiked: (onFailure)", t.getMessage());
                    }
                }, data.getPostId(), TransitUser.user.getLogin());
            } catch (JSONException e) {
                Log.d("sendToGetIsLiked: (JSONException)", e.getMessage());
            }
        } else {
            holder.like_flag = postsLibrary.getDataArrayList().get(position).isLiked();
            holder.like.setImageDrawable(context.getResources().getDrawable(postsLibrary.getDataArrayList().get(position).isLiked() ? R.drawable.like_fill_gradient : R.drawable.like_empty_gradient, context.getTheme()));
            holder.amountLikes.setText(Integer.toString(data.getLikes()));
        }

        if (postsLibrary.getDataArrayList().get(position).isSaved() == null) {
            try {
                Services.sendToGetIsSaved(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            try {
                                JSONObject object = new JSONObject(response.body());

                                // set saved
                                holder.bookmark_flag = object.getBoolean("isSaved");
                                holder.bookmark.setImageDrawable(context.getResources().getDrawable(holder.bookmark_flag ? R.drawable.bookmark_saved : R.drawable.bookmark, context.getTheme()));
                                data.setSaved(holder.bookmark_flag);
                            } catch (JSONException e) {
                                Log.d("sendToGetIsLiked: (onResponse)", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToGetIsLiked: (onFailure)", t.getMessage());
                    }
                }, data.getPostId(), TransitUser.user.getLogin());
            } catch (JSONException e) {
                Log.d("sendToGetIsLiked: (JSONException)", e.getMessage());
            }
        } else {
            holder.bookmark_flag = postsLibrary.getDataArrayList().get(position).isSaved();
            holder.bookmark.setImageDrawable(context.getResources().getDrawable(postsLibrary.getDataArrayList().get(position).isSaved() ? R.drawable.bookmark_saved : R.drawable.bookmark, context.getTheme()));
        }
        // endregion
        // region set other text info
        holder.nick.setText(data.getAuthor());
        holder.taggedPeople.setText(context.getResources().getString(R.string.tagged_people));
        holder.amountLikesTitle.setText(R.string.amount_likes_title);
        holder.authorNickname.setText(data.getAuthor());
        holder.description.setText(data.getDescription());

        Calendar calendar = DateFormatting.getCalendar(data.getDateOfAdd());
        //holder.hours.setText(DateFormatting.formatDate(calendar.getTime()));
        holder.hours.setText("10/1-/1/");
        // endregion

        holder.post_layout.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_paging));
    }

    @Override
    public int getItemCount() {
        return postsLibrary.getDataArrayList().size();
    }

    public class ViewHolderPosts extends RecyclerView.ViewHolder {
        private final LinearLayout post_layout;
        private final ImageView avaView;
        private final LinearLayout taggedPeopleLayout;
        private final TextView taggedPeople;
        private final TextView nick;
        private final ImageView content;
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
        boolean like_flag = false;
        boolean bookmark_flag = false;

        public ViewHolderPosts(@NonNull View itemView) {
            super(itemView);

            post_layout = itemView.findViewById(R.id.post_layout);

            avaView = itemView.findViewById(R.id.post_author_page);
            nick = itemView.findViewById(R.id.nick_view);

            postContext = itemView.findViewById(R.id.post_context);
            activity.registerForContextMenu(postContext); // post context registration
            content = itemView.findViewById(R.id.image_content);

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

        @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged", "ClickableViewAccessibility"})
        private void setListeners() {
            postContext.setOnTouchListener((v, event) -> {
                NewsLine.mapPost = new Pair<>(getAdapterPosition(), postsLibrary.getDataArrayList().get(getAdapterPosition()));
                return false;
            });

            taggedPeople.setOnClickListener(v -> {
                if (taggedPeopleLayout.getChildCount() > 1) {
                    taggedPeopleLayout.setVisibility(taggedPeopleLayout.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
                } else {
                    String id = postsLibrary.getDataArrayList().get(getAdapterPosition()).getPostId();
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

            like.setOnClickListener(v -> {
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

                // region send unlike
                try {
                    JSONObject object = new JSONObject();
                    object.put("postId", postsLibrary.getDataArrayList().get(position).getPostId());
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

            bookmark.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (!bookmark_flag) {
                    bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_saved, context.getTheme()));
                } else {
                    bookmark.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark, context.getTheme()));
                }

                bookmark_flag = !bookmark_flag;
                postsLibrary.getDataArrayList().get(position).setSaved(bookmark_flag);
                notifyDataSetChanged();

                // region send save
                try {
                    JSONObject object = new JSONObject();
                    object.put("postId", postsLibrary.getDataArrayList().get(position).getPostId());
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

            comment.setOnClickListener(v -> {
                NewsLine.mapPost = new Pair<>(getAdapterPosition(), postsLibrary.getDataArrayList().get(getAdapterPosition()));
                Intent intent = Intents.getComments();
                context.startActivity(intent);
            });

            send.setOnClickListener(v -> {
                // TODO: send
            });

            avaView.setOnClickListener(v -> {
                // TODO get user
            });
        }
    }
}
