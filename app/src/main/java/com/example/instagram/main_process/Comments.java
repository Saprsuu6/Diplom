package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
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
import com.example.instagram.services.Animation;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitComment;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllComments;
import com.example.instagram.services.themes_and_backgrounds.Backgrounds;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

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
    private PagingViewGetAllComments pagingView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Resources resources;
    private LinearLayout reply;
    private LinearLayout comments;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private EditText[] editTexts;
    private ImageView[] imageViews;
    static public Pair<Integer, Comment> mapComment;
    static public Comment toReply;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        setUiVisibility();

        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Animation.getAnimations(comments).start();
        setListeners();
        LoadAvatar();

        pagingView = new PagingViewGetAllComments(findViewById(R.id.scroll_view), findViewById(R.id.recycler_view), findViewById(R.id.skeleton), this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(languages);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (TransitUser.user.getLogin().equals(Comments.mapComment.second.getAuthor())) {
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
                    JSONObject body = new JSONObject();
                    body.put("commentId", Comments.mapComment.second.getCommentId());
                    body.put("token", TransitUser.user.getToken());

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
                editTexts[0].setText(Comments.mapComment.second.getContent());
                Comments.mapComment.second.setToChange(true);
                break;
        }
        return true;
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void findViews() {
        resources = getResources();
        swipeRefreshLayout = findViewById(R.id.swipe_refresh);
        reply = findViewById(R.id.reply_layout);
        comments = findViewById(R.id.comments);
        languages = findViewById(R.id.languages);
        textViews = new TextView[]{findViewById(R.id.comment_title), findViewById(R.id.replayed)};
        editTexts = new EditText[]{findViewById(R.id.comment_add)};
        imageViews = new ImageView[]{findViewById(R.id.send), findViewById(R.id.author_ava), findViewById(R.id.close_reply), findViewById(R.id.back)};
    }

    private void LoadAvatar() {
        // region send request to get avatar
        try {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avaLink = response.body();

                        // set ava
                        Glide.with(getApplicationContext()).load(Services.BASE_URL + avaLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViews[1]);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, TransitUser.user.getLogin());
        } catch (JSONException e) {
            Log.d("sendToGetAva: (JSONException)", e.getMessage());
        }
        // endregion
    }

    private void sendNewRequest() {
        PagingViewGetAllComments.isEnd = false;
        PaginationCurrentForAllComments.resetCurrent();
        pagingView.notifyAdapterToClearAll();
        swipeRefreshLayout.setRefreshing(false);
    }

    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::sendNewRequest);

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
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        imageViews[0].setOnClickListener(v -> {
            if (editTexts[0].getText().toString().equals("")) {
                Toast.makeText(this, getString(R.string.error_send_password1), Toast.LENGTH_SHORT).show();
                return;
            }


            if (Comments.mapComment != null && Comments.mapComment.second.isToChange()) {
                JSONObject changeComment = new JSONObject();

                try {
                    String newText = editTexts[0].getText().toString();

                    JSONObject comment = Comments.mapComment.second.getJSONCommentToChange(newText);
                    changeComment.put("token", TransitUser.user.getToken());
                    changeComment.put("comment", comment);
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
                TransitComment.comment.setPostId(NewsLine.mapPost.second.getPostId());
                TransitComment.comment.setContent(editTexts[0].getText().toString());
                Toast.makeText(this, getString(R.string.comment_has_been_sent), Toast.LENGTH_SHORT).show();

                try {
                    String comment;

                    if (toReply != null) {
                        TransitComment.comment.setReplayedCommentId(toReply.getCommentId());
                        comment = TransitComment.comment.getJSONReply().toString();
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
                        }, comment);
                    } else {
                        comment = TransitComment.comment.getJSONComment().toString();
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
                        }, comment);
                    }
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            }

            editTexts[0].getText().clear();

            if (reply.getVisibility() == View.VISIBLE) {
                reply.setVisibility(View.GONE);
                Comments.toReply = null;
            }
        });

        imageViews[1].setOnClickListener(v -> {
            try {
                Services.sendToGetCurrentUser(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject user;

                            try {
                                user = new JSONObject(response.body());

                                SelfPage.userPage = User.getPublicUser(user, TransitUser.user.getLogin());
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
                }, TransitUser.user.getLogin());
            } catch (JSONException e) {
                Log.d("JSONException: ", e.getMessage());
            }
        });

        // gone reply
        imageViews[2].setOnClickListener(v -> {
            reply.setVisibility(View.GONE);
            Comments.toReply = null;
        });
        // back
        imageViews[3].setOnClickListener(v -> finish());
    }

    // region Localisation
    private void setStringResources() {
        textViews[0].setText(resources.getString(R.string.comments_title));
        editTexts[0].setHint(resources.getString(R.string.add_comment));
        pagingView.notifyAllLibrary();
    }
    // endregion
}