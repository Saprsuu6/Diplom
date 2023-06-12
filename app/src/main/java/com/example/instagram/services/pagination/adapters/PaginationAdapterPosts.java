package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaginationAdapterPosts extends RecyclerView.Adapter<PaginationAdapterPosts.ViewHolderPosts> {
    private final Context context;

    private final Activity activity;
    private final PostsLibrary postsLibrary;
    private SparseIntArray positionList = new SparseIntArray();

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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull PaginationAdapterPosts.ViewHolderPosts holder, int position) {
        Post data = postsLibrary.getDataArrayList().get(position);

        assert data.getResourceImg() != null;
        if (data.getResourceImg().equals("")) {
            // set image
            Glide.with(context).load(Services.BASE_URL + data.getResourceVideo()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.content);
        } else {
            Glide.with(context).load(Services.BASE_URL + data.getResourceImg()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.content);
        }

        // region send request to get avatar
        try {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    assert response.body() != null;
                    String avaLink = response.body();

                    // set ava
                    Glide.with(context).load(Services.BASE_URL + avaLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.avaView);
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, data.getAuthor());
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }
        // endregion

        holder.nick.setText(data.getAuthor());
        holder.place.setText(data.getPlace());

        holder.like.setImageDrawable(context.getResources().getDrawable(loadSP(position + "Like") ? R.drawable.like_fill_gradient : R.drawable.like_empty_gradient, context.getTheme()));

        holder.bookmarck.setImageDrawable(context.getResources().getDrawable(loadSP(position + "Bookmark") ? R.drawable.bookmark_saved : R.drawable.bookmark, context.getTheme()));

        holder.amountLikesTitle.setText(R.string.amount_likes_title);
        holder.amountLikes.setText(Integer.toString(data.getLikes()));
        holder.authorNickname.setText(data.getAuthor());
        holder.description.setText(data.getDescription());

        if (data.getNickNames() != null) {
            holder.taggedPeople.setVisibility(View.VISIBLE);
            holder.taggedPeople.setText(context.getResources().getString(R.string.tagged_people));

            String[] taggedPeople = data.getNickNames().split(", ");

            for (String person : taggedPeople) {
                TextView taggedPerson = new TextView(context);
                taggedPerson.setTextSize(18);
                taggedPerson.setText(person);

                holder.taggedPeopleLayout.addView(taggedPerson);
            }
        }

        Calendar calendar = DateFormatting.getCalendar(data.getDateOfAdd());
        holder.hours.setText(DateFormatting.formatDate(calendar.getTime()));
    }

    @Override
    public int getItemCount() {
        return postsLibrary.getDataArrayList().size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteByPosition(int position) {
        this.postsLibrary.getDataArrayList().remove(position);
        notifyDataSetChanged();
    }

    // region SharedPreferences
    //Сохраняет флажок в SharedPreferences
    public void saveSP(String key, boolean value) {
        com.example.instagram.services.SharedPreferences.saveSP(context, key, value);
    }

    //Загружает нажатый флажок из SharedPreferences
    public boolean loadSP(String key) {
        return com.example.instagram.services.SharedPreferences.loadBoolSP(context, key);
    }

    //Удаляет нажатый флажок из SharedPreferences
    public void deleteSp(String key) {
        com.example.instagram.services.SharedPreferences.deleteSP(context, key);
    }
    // endregion SharedPreferences

    public class ViewHolderPosts extends RecyclerView.ViewHolder {
        private final ImageView avaView;
        private final LinearLayout taggedPeopleLayout;
        private final TextView taggedPeople;
        private final TextView nick;
        private final TextView place;
        private final ImageView postContext;
        private final ImageView content;
        private final ImageView like;
        boolean like_flag = false;
        private final ImageView comment;
        private final ImageView send;
        private final TextView amountLikesTitle;
        private final ImageView bookmarck;
        boolean bookmarck_flag = false;
        private final TextView amountLikes;
        private final TextView authorNickname;
        private final TextView description;
        private final TextView hours;

        public ViewHolderPosts(@NonNull View itemView) {
            super(itemView);

            avaView = itemView.findViewById(R.id.post_author_page);
            nick = itemView.findViewById(R.id.nick_view);
            place = itemView.findViewById(R.id.place);
            postContext = itemView.findViewById(R.id.post_context);

            activity.registerForContextMenu(postContext); // post context registration

            content = itemView.findViewById(R.id.image_content);

            like = itemView.findViewById(R.id.post_like);
            comment = itemView.findViewById(R.id.comment);
            send = itemView.findViewById(R.id.send_post);
            bookmarck = itemView.findViewById(R.id.bookmark);

            amountLikesTitle = itemView.findViewById(R.id.amount_likes_title);
            amountLikes = itemView.findViewById(R.id.amount_likes);
            authorNickname = itemView.findViewById(R.id.author_nickname);
            description = itemView.findViewById(R.id.description);

            taggedPeopleLayout = itemView.findViewById(R.id.tagged_people_layout);
            taggedPeople = itemView.findViewById(R.id.tagged_people);

            hours = itemView.findViewById(R.id.hours);

            NewsLine.textViews.add(amountLikesTitle);
            NewsLine.textViews.add(hours);
            NewsLine.textViews.add(taggedPeople);

            setListeners();
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setListeners() {
            taggedPeople.setOnClickListener(v -> {
                // TODO show user page
            });

            like.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (!like_flag) {
                    like_flag = true;
                    saveSP(position + "Like", true);
                    like.setImageDrawable(context.getResources().getDrawable(R.drawable.like_fill_gradient, context.getTheme()));
                } else {
                    like_flag = false;
                    deleteSp(position + "Like");
                    like.setImageDrawable(context.getResources().getDrawable(R.drawable.like_empty_gradient, context.getTheme()));
                }
            });

            comment.setOnClickListener(v -> {
                Intent intent = Intents.getComments();
                context.startActivity(intent);
            });

            send.setOnClickListener(v -> {
                // TODO: send
            });

            bookmarck.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (!bookmarck_flag) {
                    bookmarck_flag = true;
                    saveSP(position + "Bookmark", true);
                    bookmarck.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark_saved, context.getTheme()));
                } else {
                    bookmarck_flag = false;
                    deleteSp(position + "Bookmark");
                    bookmarck.setImageDrawable(context.getResources().getDrawable(R.drawable.bookmark, context.getTheme()));
                }
            });
        }
    }
}
