package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Comment;
import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Services;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllComments;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Comments extends AppCompatActivity {
    private class Views {
        private final LinearLayout replyLayout;
        private final ImageView arrowBack;
        private final ImageView authorAva;
        private final ImageView send;
        private final ImageView closeReply;
        private final EditText message;
        private final TextView title;
        private final Spinner languagesSpinner;
        private final SwipeRefreshLayout swipeRefreshLayout;

        public Views() {
            replyLayout = findViewById(R.id.reply_layout);
            closeReply = findViewById(R.id.close_reply);
            arrowBack = findViewById(R.id.back);
            authorAva = findViewById(R.id.author_ava);
            send = findViewById(R.id.send);
            message = findViewById(R.id.comment_add);
            title = findViewById(R.id.comment_title);
            languagesSpinner = findViewById(R.id.languages);
            swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        }
    }

    public static class CommentToAdd {
        public static String commentId;
        public static String postId;
        public static String content;
    }

    private PagingViewGetAllComments pagingView;
    private Resources resources;
    private Localisation localisation;
    static public Pair<Integer, Comment> mapComment;
    static public Comment toReply;
    private Views views;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        views = new Views();
        resources = getResources();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        setUiVisibility();
        setListeners();
        LoadAvatar();

        pagingView = new PagingViewGetAllComments(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
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
                try {
                    String token = Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString());
                    String commentId = Comments.mapComment.second.getCommentId();
                    JSONObject body = Comment.getJSONToDeleteComment(commentId, token);

                    Services.sendToDeleteComment(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                List<String> deleteId = null;

                                try {
                                    deleteId = new ArrayList<>();
                                    JSONObject jsonObject = new JSONObject(response.body());

                                    for (int i = 0; i < jsonObject.length(); i++) {
                                        deleteId.add(jsonObject.getString(Integer.toString(i)));
                                    }

                                } catch (JSONException e) {
                                    Log.d("isSuccessful: (JSONException)", e.getMessage());
                                }

                                Toast.makeText(getApplicationContext(), R.string.successful_delete, Toast.LENGTH_SHORT).show();
                                pagingView.notifyAdapterToClearByPosition(deleteId);
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("onFailure: (sendToDeletePost)", t.getMessage());
                        }
                    }, body.toString());
                } catch (JSONException e) {
                    Log.d("JSONException: (sendToDeletePost)", e.getMessage());
                }
                break;
            case R.id.change_comment:
                views.message.setText(Comments.mapComment.second.getContent());
                Comments.mapComment.second.setToChange(true);
                break;
        }
        return true;
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void LoadAvatar() {
        // region send request to get avatar
        String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());

        try {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avaLink = response.body();
                        String imagePath = Services.BASE_URL + getString(R.string.root_folder) + avaLink;
                        Glide.with(getApplicationContext()).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(views.authorAva);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, login);
        } catch (JSONException e) {
            Log.d("sendToGetAva: (JSONException)", e.getMessage());
        }
        // endregion
    }

    private void sendNewRequest() {
        PagingViewGetAllComments.isEnd = false;
        PaginationCurrentForAllComments.resetCurrent();
        pagingView.notifyAdapterToClearAll();
        views.swipeRefreshLayout.setRefreshing(false);
    }

    private void setListeners() {
        views.swipeRefreshLayout.setOnRefreshListener(this::sendNewRequest);

        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();

                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

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
                    JSONObject changeComment = Comment.getJSONToSendCangeCommnet(comment.toString(), token);

                    pagingView.notifyLibraryByPosition(Comments.mapComment.first, newText);

                    Comments.mapComment.second.setToChange(false);
                    Services.sendToChangeComment(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            Log.d("sendToChangeComment: (onResponse)", response.body());
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToChangeComment: (onFailure)", t.getMessage());
                        }
                    }, changeComment.toString());
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            } else {
                CommentToAdd.postId = NewsLine.mapPost.second.getPostId();
                CommentToAdd.content = views.message.getText().toString().trim();
                Toast.makeText(this, getString(R.string.comment_has_been_sent), Toast.LENGTH_SHORT).show();

                try {
                    if (toReply != null) {
                        CommentToAdd.commentId = toReply.getCommentId();
                        JSONObject jsonObject = Comment.getJSONReply(login, CommentToAdd.commentId, CommentToAdd.postId, CommentToAdd.content);
                        Services.sendToAddNewAnswer(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                Log.d("sendToAddNewAnswer: (onResponse)", response.body());
                                if (response.isSuccessful()) {
                                    sendNewRequest();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.d("sendToAddNewAnswer: (onFailure)", t.getMessage());
                            }
                        }, jsonObject.toString());
                    } else {
                        JSONObject jsonObject = Comment.getJSONComment(CommentToAdd.postId, login, CommentToAdd.content);
                        Services.sendToAddNewComment(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                Log.d("sendToAddNewComment: (onResponse)", response.body());
                                if (response.isSuccessful()) {
                                    sendNewRequest();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.d("sendToAddNewComment: (onFailure)", t.getMessage());
                            }
                        }, jsonObject.toString());
                    }
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            }

            views.message.getText().clear();

            if (views.replyLayout.getVisibility() == View.VISIBLE) {
                views.replyLayout.setVisibility(View.GONE);
                Comments.toReply = null;
            }
        });

        views.authorAva.setOnClickListener(v -> {
            String login = Cache.loadStringSP(getApplicationContext(), CacheScopes.USER_LOGIN.toString());

            try {
                Services.sendToGetCurrentUser(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject user;

                            try {
                                user = new JSONObject(response.body());

                                SelfPage.userPage = User.getPublicUser(user, login);
                                startActivity(Intents.getSelfPage());
                            } catch (JSONException | ParseException e) {
                                Log.d("JSONException", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToGetCurrentUser: (onFailure)", t.getMessage());
                    }
                }, login);
            } catch (JSONException e) {
                Log.d("JSONException: ", e.getMessage());
            }
        });

        // gone reply
        views.closeReply.setOnClickListener(v -> {
            views.replyLayout.setVisibility(View.GONE);
            Comments.toReply = null;
        });
        // back
        views.arrowBack.setOnClickListener(v -> finish());
    }

    private void setStringResources() {
        views.title.setText(resources.getString(R.string.comments_title));
        views.message.setHint(resources.getString(R.string.add_comment));
        pagingView.notifyAllLibrary();
    }
}