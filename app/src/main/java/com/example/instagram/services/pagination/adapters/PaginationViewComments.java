package com.example.instagram.services.pagination.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import com.example.instagram.DAOs.Comment;
import com.example.instagram.DAOs.CommentsLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.Comments;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;

import org.json.JSONException;

public class PaginationViewComments extends RecyclerView.Adapter<PaginationViewComments.ViewHolderComments> {
    private final Context context;
    private final Activity activity;
    private final CommentsLibrary commentsLibrary;

    public CommentsLibrary getCommentsLibrary() {
        return commentsLibrary;
    }

    public PaginationViewComments(@Nullable Activity activity, Context context, CommentsLibrary commentsLibrary) {
        this.activity = activity;
        this.context = context;
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

        // region send request to get avatar
        try {
            new DoCallBack().setValues(null, context, new Object[]{data.getAuthor(), holder.ava}).sendToGetAvaImage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // endregion


        // region set content of comment
        holder.date.setText(DateFormatting.formatDate(data.getDateOfAdd()));
        holder.authorTitle.setText(context.getResources().getString(R.string.author) + ": ");
        holder.author.setText(data.getAuthor());
        holder.content.setText(data.getContent());
        holder.reply.setText(R.string.reply);
        // endregion

        // region answer to
        if (data.getReplayedCommentId() != null) {
            holder.answerToLayout.setVisibility(View.VISIBLE);
            holder.answerToHeader.setText(R.string.answer_to);

            Comment comment = commentsLibrary.getCommentList().stream().filter(item -> item.getCommentId().equals(data.getReplayedCommentId())).findAny().orElse(null);

            if (comment != null) {
                holder.answerToAuthor.setText(comment.getAuthor());
                holder.answerToContent.setText(comment.getContent());
                holder.answerToDate.setText(DateFormatting.formatDate(comment.getDateOfAdd()));
            }
        }
        // endregion

        holder.commentObj.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_paging));
    }

    @Override
    public int getItemCount() {
        return commentsLibrary.getCommentList().size();
    }

    public class ViewHolderComments extends RecyclerView.ViewHolder {
        private final LinearLayout commentObj;
        private final LinearLayout comment;
        private final ImageView ava;
        private final TextView authorTitle;
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
            authorTitle = itemView.findViewById(R.id.author_title);
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
                reply.setVisibility(View.VISIBLE);

                TextView replayed = activity.findViewById(R.id.replayed);
                replayed.setText(commentsLibrary.getCommentList().get(getAdapterPosition()).getAuthor() + ": " + commentsLibrary.getCommentList().get(getAdapterPosition()).getContent());

                Comments.toReply = commentsLibrary.getCommentList().get(getAdapterPosition());
            });

            // set user page
            ava.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{author.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            // set user page
            author.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{author.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            // set user page
            answerToAuthor.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{author.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
