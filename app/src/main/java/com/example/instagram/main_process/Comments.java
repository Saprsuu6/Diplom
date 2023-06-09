package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.DAOs.Comment;
import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Resources;
import com.example.instagram.services.SetImagesGlide;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.paging_views.PagingAdapterComments;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;
import org.json.JSONObject;

public class Comments extends AppCompatActivity {
    private class Views {
        private final LinearLayout commentsLayout;
        private final LinearLayout replyLayout;
        private final ImageView arrowBack;
        private final ImageView authorAva;
        private final ImageView send;
        private final ImageView closeReply;
        private final EditText message;
        private final SwipeRefreshLayout swipeRefreshLayout;

        public Views() {
            commentsLayout = findViewById(R.id.comments);
            replyLayout = findViewById(R.id.reply_layout);
            closeReply = findViewById(R.id.close_reply);
            arrowBack = findViewById(R.id.back);
            authorAva = findViewById(R.id.author_ava);
            send = findViewById(R.id.send);
            message = findViewById(R.id.comment_add);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        }
    }

    public static class CommentToAdd {
        public static String commentId;
        public static String postId;
        public static String content;
    }

    private PagingAdapterComments pagingView;
    static public Pair<Integer, Comment> mapComment;
    static public Comment toReply;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        views = new Views();

        setListeners();
        UiVisibility.setUiVisibility(this);

        try {
            LoadAvatar();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        pagingView = new PagingAdapterComments(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemesBackgrounds.loadBackground(this, views.commentsLayout);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
        if (login.equals(Comments.mapComment.second.getAuthor())) {
            MenuInflater inflater = getMenuInflater();

            if (v.getId() == R.id.comment) {
                inflater.inflate(R.menu.comment_context_menu, menu);
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.remove_post:
                String token = Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString());
                String commentId = Comments.mapComment.second.getCommentId();

                try {
                    JSONObject jsonObject = Comment.getJSONToDeleteComment(commentId, token);
                    pagingView.notifyAdapterToClearByPosition(commentId);
                    new DoCallBack().setValues(null, this, new Object[]{jsonObject, pagingView}).sendToDeleteComment();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            case R.id.change_comment:
                Resources.setText(Comments.mapComment.second.getContent(), views.message);
                Comments.mapComment.second.setToChange(true);
                break;
        }
        return true;
    }

    private void LoadAvatar() throws JSONException {
        // region send request to get avatar
        String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
        new DoCallBack().setValues(null, this, new Object[]{login, views.authorAva}).sendToGetAvaImage();
        // endregion
    }

    private void sendNewRequest() {
        PagingAdapterComments.isEnd = false;
        PaginationCurrentForAllComments.resetCurrent();
        pagingView.notifyAdapterToClearAll();
        views.swipeRefreshLayout.setRefreshing(false);
    }

    private void setListeners() {
        views.swipeRefreshLayout.setOnRefreshListener(this::sendNewRequest);

        views.send.setOnClickListener(v -> {
            String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());

            if (views.message.getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
                return;
            }

            if (Comments.mapComment != null && Comments.mapComment.second.isToChange()) {


                try {
                    String newText = views.message.getText().toString();

                    String token = Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString());
                    JSONObject comment = Comments.mapComment.second.getJSONCommentToChange(newText, login);
                    JSONObject jsonObject = Comment.getJSONToSendCangeCommnet(comment.toString(), token);
                    Comments.mapComment.second.setToChange(false);

                    pagingView.notifyLibraryByPosition(Comments.mapComment.first, newText);
                    new DoCallBack().setValues(null, this, new Object[]{jsonObject}).sendToChangeComment();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            } else {
                CommentToAdd.postId = NewsLine.mapPost.second.getPostId();
                CommentToAdd.content = views.message.getText().toString().trim();
                Resources.getToast(this, getString(R.string.comment_has_been_sent)).show();
                JSONObject jsonObject;

                try {
                    if (toReply != null) {
                        CommentToAdd.commentId = toReply.getCommentId();
                        jsonObject = Comment.getJSONReply(login, CommentToAdd.commentId, CommentToAdd.postId, CommentToAdd.content);
                        jsonObject.put("token", Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString()));
                        new DoCallBack().setValues(this::sendNewRequest, this, new Object[]{jsonObject}).sendToAddNewAnswer();
                    } else {
                        jsonObject = Comment.getJSONComment(CommentToAdd.postId, login, CommentToAdd.content);
                        jsonObject.put("token", Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString()));
                        new DoCallBack().setValues(this::sendNewRequest, this, new Object[]{jsonObject}).sendToAddNewComment();
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            views.message.getText().clear();

            if (views.replyLayout.getVisibility() == View.VISIBLE) {
                Resources.setVisibility(View.GONE, views.replyLayout);
                Comments.toReply = null;
            }
        });

        views.authorAva.setOnClickListener(v -> {
            String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());

            try {
                new DoCallBack().setValues(() -> startActivity(Intents.getSelfPage()), this, new Object[]{login}).sendToGetCurrentUser();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        // gone reply
        views.closeReply.setOnClickListener(v -> {
            Resources.setVisibility(View.GONE, views.replyLayout);
            Comments.toReply = null;
        });
        // back
        views.arrowBack.setOnClickListener(v -> finish());
    }
}