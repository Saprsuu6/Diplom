package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
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
import com.example.instagram.DAOs.MainData;
import com.example.instagram.DAOs.MainDataLibrary;
import com.example.instagram.R;
import com.example.instagram.authentication.after_reg.FindContactsFriends;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.Intents;
import com.example.instagram.services.TransitUser;

import java.util.ArrayList;

public class PaginationAdapterPosts extends RecyclerView.Adapter<PaginationAdapterPosts.ViewHolderPosts> {
    private final Context context;

    @Nullable
    private final Activity activity;
    private final MainDataLibrary mainDataLibrary;
    private SparseIntArray positionList = new SparseIntArray();


    public PaginationAdapterPosts(@Nullable Activity activity, Context context, MainDataLibrary mainDataLibrary) {
        this.activity = activity;
        this.context = context;
        this.mainDataLibrary = mainDataLibrary;
    }

    @NonNull
    @Override
    public PaginationAdapterPosts.ViewHolderPosts onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_post, parent, false); // don't forget to change

        return new ViewHolderPosts(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull PaginationAdapterPosts.ViewHolderPosts holder, int position) {
        MainData data = mainDataLibrary.getDataArrayList().get(position);

        // set image
        Glide.with(context).load(data.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.content);

        // set ava
        Glide.with(context).load(data.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.avaView);

        holder.nick.setText(data.getName());
        holder.place.setText(data.getName());

        holder.like.setImageDrawable(context.getResources()
                .getDrawable(loadSP(position + "Like")
                        ? R.drawable.like_fill_gradient
                        : R.drawable.like_empty_gradient, context.getTheme()));

        holder.bookmarck.setImageDrawable(context.getResources()
                .getDrawable(loadSP(position + "Bookmark")
                        ? R.drawable.bookmark_saved
                        : R.drawable.bookmark, context.getTheme()));

        holder.amountLikesTitle.setText(R.string.amount_likes_title);
        holder.amountLikes.setText(data.getName());
        holder.authorNickname.setText(data.getName());
        holder.description.setText(data.getName());
        holder.hours.setText(data.getName());
        holder.hoursText.setText(R.string.hours_ago);

        // TODO change language in activity
    }

    @Override
    public int getItemCount() {
        return mainDataLibrary.getDataArrayList().size();
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
        private final TextView hoursText;

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
            hours = itemView.findViewById(R.id.hours);
            hoursText = itemView.findViewById(R.id.hours_ago_title);

            NewsLine.textViews.add(amountLikesTitle);
            NewsLine.textViews.add(hoursText);

            setListeners();
        }

        @SuppressLint("UseCompatLoadingForDrawables")
        private void setListeners() {
            postContext.setOnClickListener(v -> {
                // TODO call post context
            });

            like.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (!like_flag) {
                    like_flag = true;
                    saveSP(position + "Like", true);
                    like.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.like_fill_gradient, context.getTheme()));
                } else {
                    like_flag = false;
                    deleteSp(position + "Like");
                    like.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.like_empty_gradient, context.getTheme()));
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
                    bookmarck.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.bookmark_saved, context.getTheme()));
                } else {
                    bookmarck_flag = false;
                    deleteSp(position + "Bookmark");
                    bookmarck.setImageDrawable(context.getResources()
                            .getDrawable(R.drawable.bookmark, context.getTheme()));
                }
            });
        }
    }
}
