package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Comment;
import com.example.instagram.DAOs.CommentsLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.Comments;
import com.example.instagram.services.Cache;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Resources;
import com.example.instagram.services.SetImagesGlide;

import org.json.JSONException;

public class PaginationViewComments extends RecyclerView.Adapter<PaginationViewComments.ViewHolderComments> {
    private final Activity activity;
    private final CommentsLibrary commentsLibrary;

    public CommentsLibrary getCommentsLibrary() {
        return commentsLibrary;
    }

    public PaginationViewComments(Activity activity, CommentsLibrary commentsLibrary) {
        this.activity = activity;
        this.commentsLibrary = commentsLibrary;
    }

    @NonNull
    @Override
    public PaginationViewComments.ViewHolderComments onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comment, parent, false);
        return new PaginationViewComments.ViewHolderComments(view);
    }

    @SuppressLint({"RtlHardcoded", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull PaginationViewComments.ViewHolderComments holder, int position) {
        Comment data = commentsLibrary.getCommentList().get(position);

        // cache
        String commentId = Cache.loadStringSP(activity, data.getCommentId());
        Cache.saveSP(activity, data.getPostId(), data.getCommentId());

        // region send request to get avatar
        String avaLink = Cache.loadStringSP(activity, data.getAuthor()+".ava");
        if (!avaLink.equals("")) {
            Glide.with(activity.getApplicationContext()).load(avaLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.ava);
        } else {
            try {
                new DoCallBack().setValues(null, activity, new Object[]{data.getAuthor(), holder.ava}).sendToGetAvaImage();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        // endregion

        // region set content of comment
        Resources.setText(DateFormatting.formatDate(data.getDateOfAdd()), holder.date);
        Resources.setText(data.getAuthor(), holder.author);
        Resources.setText(data.getContent(), holder.content);
        // endregion

        // region answer to
        if (data.getReplayedCommentId() != null) {
            Resources.setVisibility(View.VISIBLE, holder.answerToLayout);
            Resources.setText(activity.getString(R.string.answer_to), holder.answerToHeader);

            Comment comment = commentsLibrary.getCommentList().stream().filter(item -> item.getCommentId().equals(data.getReplayedCommentId())).findAny().orElse(null);

            if (comment != null) {
                Resources.setText(comment.getAuthor(), holder.answerToAuthor);
                Resources.setText(comment.getContent(), holder.answerToContent);
                Resources.setText(DateFormatting.formatDate(comment.getDateOfAdd()), holder.answerToDate);
                holder.answerToDate.setText(DateFormatting.formatDate(comment.getDateOfAdd()));
            }
        }
        // endregion

        Resources.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.anim_paging), holder.commentObj);
    }

    @Override
    public int getItemCount() {
        return commentsLibrary.getCommentList().size();
    }

    public class ViewHolderComments extends RecyclerView.ViewHolder {
        private final LinearLayout commentObj;
        private final LinearLayout comment;
        private final ImageView ava;
        private final TextView author;
        private final TextView date;
        private final TextView content;
        private final TextView reply;
        private final LinearLayout answerToLayout;
        private final TextView answerToHeader;
        private final TextView answerToAuthor;
        private final TextView answerToContent;
        private final TextView answerToDate;

        public ViewHolderComments(@NonNull View itemView) {
            super(itemView);

            commentObj = itemView.findViewById(R.id.comment_obj);
            comment = itemView.findViewById(R.id.comment);
            activity.registerForContextMenu(comment);

            ava = itemView.findViewById(R.id.ava);
            author = itemView.findViewById(R.id.author);
            date = itemView.findViewById(R.id.date);
            content = itemView.findViewById(R.id.content);
            reply = itemView.findViewById(R.id.reply);

            answerToLayout = itemView.findViewById(R.id.answer_to_layout);
            answerToHeader = itemView.findViewById(R.id.answer_to_header);
            answerToAuthor = itemView.findViewById(R.id.answer_to_author);
            answerToContent = itemView.findViewById(R.id.answer_to_content);
            answerToDate = itemView.findViewById(R.id.answer_to_date);

            setListeners();
        }

        @SuppressLint({"SetTextI18n", "ClickableViewAccessibility"})
        public void setListeners() {
            comment.setOnTouchListener((v, event) -> {
                Comments.mapComment = new Pair<>(getAdapterPosition(), commentsLibrary.getCommentList().get(getAdapterPosition()));
                return false;
            });

            reply.setOnClickListener(v -> {
                LinearLayout reply = activity.findViewById(R.id.reply_layout);
                Resources.setVisibility(View.VISIBLE, reply);

                TextView replayed = activity.findViewById(R.id.replayed);
                Resources.setText(commentsLibrary.getCommentList().get(getAdapterPosition()).getAuthor() + ": " + commentsLibrary.getCommentList().get(getAdapterPosition()).getContent(), replayed);

                Comments.toReply = commentsLibrary.getCommentList().get(getAdapterPosition());
            });

            // set user page
            ava.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{author.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            // set user page
            author.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{author.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            // set user page
            answerToAuthor.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{author.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
