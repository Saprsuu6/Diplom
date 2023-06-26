package com.example.instagram.services.pagination.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.R;

public class PaginationAdapterUsers extends RecyclerView.Adapter<PaginationAdapterUsers.ViewHolderUser> {
    //private final Activity activity;
    private final Context context;
    private final PostsLibrary postsLibrary;

    public PaginationAdapterUsers(Context context, PostsLibrary postsLibrary) {
        this.context = context;
        this.postsLibrary = postsLibrary;
    }

    @NonNull
    @Override
    public PaginationAdapterUsers.ViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_contacts, parent, false); // don't forget to change

        return new ViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaginationAdapterUsers.ViewHolderUser holder, int position) {
        Post data = postsLibrary.getDataArrayList().get(position);

        // set image
        Glide.with(context).load(data.getResourceMedia()).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.avaView);

        // set name nickname
        holder.nickView.setText(data.getAuthor());
        holder.nameView.setText(data.getAuthor());

        // set button
        holder.subscribe.setText(!holder.subscribe.isChecked() ? context.getString(R.string.subscribe_btn) : context.getString(R.string.unsubscribe_btn));
    }

    @Override
    public int getItemCount() {
        return postsLibrary.getDataArrayList().size();
    }

    public class ViewHolderUser extends RecyclerView.ViewHolder {
        private final ImageView avaView;
        private final TextView nickView;
        private final TextView nameView;
        private final CheckBox subscribe;

        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);

            avaView = itemView.findViewById(R.id.ava_view);
            nickView = itemView.findViewById(R.id.nick_view);
            nameView = itemView.findViewById(R.id.name_view);
            subscribe = itemView.findViewById(R.id.btn_subscribe);

            subscribe.setOnClickListener(v -> {
                subscribe.setChecked(subscribe.isChecked());
                subscribe.setText(!subscribe.isChecked() ? context.getString(R.string.subscribe_btn) : context.getString(R.string.unsubscribe_btn));
            });
        }
    }
}