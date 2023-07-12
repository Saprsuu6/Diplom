package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.TooltipCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.Comment;
import com.example.instagram.DAOs.CommentsLibrary;
import com.example.instagram.DAOs.NotificationsLibrary;
import com.example.instagram.DAOs.Post;
import com.example.instagram.DAOs.PostsLibrary;
import com.example.instagram.DAOs.User;
import com.example.instagram.DAOs.UsersLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.UserPage;
import com.example.instagram.services.interfaces.CallBack;
import com.example.instagram.services.pagination.PaginationCurrentForAllComments;
import com.example.instagram.services.pagination.PaginationCurrentForAllPosts;
import com.example.instagram.services.pagination.PaginationCurrentForAllPostsInCellsPosts;
import com.example.instagram.services.pagination.PaginationCurrentForAllUsers;
import com.example.instagram.services.pagination.adapters.PaginationViewNotifications;
import com.example.instagram.services.pagination.paging_views.PagingAdapterComments;
import com.example.instagram.services.pagination.paging_views.PagingAdapterNotifications;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPosts;
import com.example.instagram.services.pagination.paging_views.PagingAdapterPostsCells;
import com.example.instagram.services.pagination.paging_views.PagingAdapterUsers;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DoCallBack implements CallBack {
    @Nullable
    private Runnable runnable;
    private Activity activity;
    private Object[] params;

    public DoCallBack setValues(Runnable runnable, Activity activity, Object[] params) {
        this.runnable = runnable;
        this.activity = activity;
        this.params = params;
        return this;
    }

    @Override
    public void sendToDeletePost() throws JSONException {
        if (params != null) {
            Services.sendToDeletePost(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseStr = response.body();
                        Errors.delete(activity, responseStr).show();
                        if (runnable != null) runnable.run();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("onFailure: (sendToDeletePost)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToLikeUnlikePost() throws JSONException {
        if (params != null) {
            if (runnable != null) runnable.run();
            else {
                Services.sendToLikeUnlikePost(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Log.d("sendToLikeUnlikePost: (onResponse)", response.body());
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToLikeUnlikePost: (onFailure)", t.getMessage());
                    }
                }, params[0].toString());
            }
        }
    }

    @Override
    public void sendToLogIn() {
        if (params != null) {
            String jsonObject = params[0].toString();
            Services.authorizeUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseStr = response.body();
                        // region get token from response
                        int indexFrom = responseStr.indexOf(":");
                        String token = responseStr.substring(indexFrom + 1).trim();
                        // save auth sated user info in cache
                        Cache.saveSP(activity, CacheScopes.USER_TOKEN.toString(), token);

                        if (response.body().contains("0") && runnable != null) {
                            runnable.run();
                        } else {
                            Resources.getToast(activity, activity.getString(R.string.user_not_authorised)).show();
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, jsonObject);
        }
    }

    public void sendToSinUp() throws JSONException, IOException {
        if (params != null) {
            Services.addUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseStr = response.body();

                        // region get token from response
                        int indexFrom = responseStr.indexOf(":");
                        String token = responseStr.substring(indexFrom + 1).trim();
                        // endregion

                        if (runnable != null) runnable.run();

                        // save token
                        Cache.saveSP(activity, CacheScopes.USER_TOKEN.toString(), token.trim());
                        Errors.registrationUser(activity, response.body()).show();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToCodeToRestorePassword() {
        if (params != null) {
            Services.sendToForgotPassword(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Errors.forgotPasswordCode(activity, response.body()).show();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToCheckUsedLickInMailPassword() throws JSONException {
        if (params != null) {
            Services.sendToCheckUsedLickInMail(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().contains("0")) {
                            int index = response.body().indexOf(":");
                            String code = response.body().substring(index + 1);
                            // save user repair password email
                            Cache.saveSP(activity, CacheScopes.USER_EMAIL_CODE.toString(), code.trim());
                            if (runnable != null) runnable.run();
                        } else {
                            ((Handler) params[1]).postDelayed((Runnable) params[2], 5000L);
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToCheckUsedLickInMailEmail() throws JSONException {
        if (params != null) {
            Services.sendToCheckUsedLickInMail(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        if (response.body().contains("0")) {
                            String responseStr = response.body();
                            int index = responseStr.indexOf(":");
                            responseStr = responseStr.substring(index + 1);
                            ((EditText) params[1]).setText(responseStr.trim());
                        } else {
                            ((Handler) params[2]).postDelayed((Runnable) params[3], 5000L);
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToCheckUserCode() {
        if (params != null) {
            Services.sendToCheckUserCode(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull retrofit2.Response<String> response) {
                    String strResponse = response.body();
                    assert strResponse != null;
                    assert response.body() != null;

                    Errors.enterCode(activity, response.body()).show();

                    if (strResponse.contains("1")) {
                        char symbol = strResponse.charAt(strResponse.length() - 1);
                        int attempts = Integer.parseInt(String.valueOf(symbol));

                        if (attempts > 0) {
                            Resources.getToast(activity, String.valueOf(attempts)).show();
                        } else {
                            if (params[2] != null) ((Runnable) params[2]).run();
                        }
                    }

                    int index = response.body().indexOf(":");
                    String code = response.body().substring(index + 1);

                    // save user repair password email
                    Cache.saveSP(activity, CacheScopes.USER_EMAIL_CODE.toString(), code.trim());

                    if (runnable != null) runnable.run();


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println("Error: " + t.getMessage());
                }
            }, params[0].toString(), params[1].toString());
        }
    }

    @Override
    public void sendNewPasswordAfterForgot() {
        if (params != null) {
            Services.sendNewPasswordAfterForgot(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    assert response.body() != null;
                    Errors.afterRestorePassword(activity, response.body()).show();

                    if (response.body().contains("0")) {
                        // delete unnecessary
                        Cache.deleteSP(activity, CacheScopes.USER_EMAIL_CODE.toString());
                        if (runnable != null) runnable.run();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    System.out.println(t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendMultipartPost() throws JSONException {
        if (params != null) {
            Services.sendMultipartPost(new Callback<>() {
                @Override
                public void onResponse(@NotNull Call<String> call, @NotNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Resources.getToast(activity, activity.getString(R.string.successfully_loaded_0)).show();
                    }
                }

                @Override
                public void onFailure(@NotNull Call<String> call, @NotNull Throwable t) {
                    Log.d("sendMultipartPost: ", t.getMessage());
                }
            }, ((RequestBody) params[0]), params[1].toString());
        }
    }

    @Override
    public void sendAva() {
        if (params != null) {
            Services.sendAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseStr = response.body();
                        Errors.sendAvatar(activity, responseStr).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendAvaToBackEnd: ", t.getMessage());
                }
            }, (RequestBody) params[0], params[1].toString());
        }
    }

    @Override
    public void sendToGetAvaImage() throws JSONException {
        if (params != null) {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avaLink = response.body();
                        String link = GetMediaLink.getMediaLink(activity, avaLink);

                        try {
                            Cache.saveSP(activity, params[2].toString() + "." + "authorAvatar", link);
                        } catch (Exception e) {
                            System.out.println(e.getMessage());
                        }

                        // if imageViews more then one
                        try {
                            Glide.with(activity.getApplicationContext()).load(link).diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) params[1]);
                            Glide.with(activity.getApplicationContext()).load(link).diskCacheStrategy(DiskCacheStrategy.ALL).into((ImageView) params[2]);
                        } catch (Exception e) {
                            Log.d("DoCallBack: ", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetAvaInTagPeople() throws JSONException {
        if (params != null) {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.body() != null) {
                        String avaLink = response.body();

                        if (avaLink.equals("")) {
                            ((ImageView) params[1]).setVisibility(View.VISIBLE);
                            ((ImageView) params[2]).setVisibility(View.GONE);
                            if (runnable != null) runnable.run();

                            TooltipCompat.setTooltipText(((ImageView) params[1]), activity.getResources().getString(R.string.user_are_not_exists));
                        } else {
                            // set ava
                            String link = GetMediaLink.getMediaLink(activity, avaLink);
                            Glide.with(activity.getApplicationContext()).load(link).diskCacheStrategy(DiskCacheStrategy.ALL).into(((ImageView) params[2]));

                            ((ImageView) params[1]).setVisibility(View.GONE);
                            ((ImageView) params[2]).setVisibility(View.VISIBLE);
                            if (runnable != null) runnable.run();
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetCurrentUser() throws JSONException {
        if (params != null) {
            Services.sendToGetCurrentUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        JSONObject user;

                        try {
                            user = new JSONObject(response.body());
                            UserPage.userPage = User.getPublicUser(user, params[0].toString());
                            if (runnable != null) runnable.run();
                        } catch (JSONException | ParseException e) {
                            Log.d("JSONException", e.getMessage());
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetCurrentUser: (onFailure)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetTaggedPeople() throws JSONException {
        if (params != null) {
            Services.sendToGetTaggedPeople(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.code() == 200) {
                        try {
                            assert response.body() != null;
                            JSONObject logins = new JSONObject(response.body());

                            for (int i = 0; i < logins.length(); i++) {
                                String login = logins.getString(Integer.toString(i));
                                TextView taggedPerson = new TextView(activity);
                                taggedPerson.setTextAppearance(R.style.Text);
                                taggedPerson.setText(login);

                                // set user page
                                taggedPerson.setOnClickListener(v1 -> {
                                    try {
                                        new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{login}).sendToGetCurrentUser();
                                    } catch (JSONException e) {
                                        throw new RuntimeException(e);
                                    }
                                });

                                ((LinearLayout) params[1]).addView(taggedPerson);
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
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetIsLikedForDialogPost() throws JSONException {
        if (params != null) {
            Services.sendToGetIsLiked(new Callback<>() {
                @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body());

                            // set liked
                            boolean isLiked = object.getBoolean("isLiked");
                            Resources.setDrawableIntoImageView(activity.getResources().getDrawable(isLiked ? R.drawable.like_fill : R.drawable.like_empty, activity.getTheme()), ((ImageView) params[2]));
                            ((Post) params[4]).setLiked(isLiked);

                            // set amount likes
                            int likes = object.getInt("amountLikes");
                            Resources.setText(Integer.toString(likes), ((TextView) params[3]));
                            ((Post) params[4]).setLikes(likes);

                            Cache.saveSP(activity, params[0].toString() + "." + "isLiked", isLiked);
                            Cache.saveSP(activity, params[0].toString() + "." + "likes", likes);
                            if (runnable != null) runnable.run();
                        } catch (JSONException e) {
                            Log.d("sendToGetIsLiked: (onResponse)", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetIsLiked: (onFailure)", t.getMessage());
                }
            }, params[0].toString(), params[1].toString());
        }
    }

    @Override
    public void sendToSaveUnsavedPost() throws JSONException {
        if (params != null) {
            Services.sendToSaveUnsavedPost(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    Log.d("sendToSaveUnsavedPost: (onResponse)", response.body());
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToSaveUnsavedPost: (onFailure)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetIsSaved() throws JSONException {
        if (params != null) {
            Services.sendToGetIsSaved(new Callback<>() {
                @SuppressLint("UseCompatLoadingForDrawables")
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        try {
                            JSONObject object = new JSONObject(response.body());

                            // set saved
                            boolean isSaved = object.getBoolean("isSaved");
                            Resources.setDrawableIntoImageView(activity.getResources().getDrawable(isSaved ? R.drawable.bookmark_saved : R.drawable.bookmark, activity.getTheme()), ((ImageView) params[2]));
                            ((Post) params[3]).setSaved(isSaved);

                            Cache.saveSP(activity, params[0].toString() + "." + "isSaved", isSaved);
                        } catch (JSONException e) {
                            Log.d("sendToGetIsLiked: (onResponse)", e.getMessage());
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetIsLiked: (onFailure)", t.getMessage());
                }
            }, params[0].toString(), params[1].toString());// postId, login, bookmark_flag(boolean), save, post
        }
    }

    @Override
    public void sendToAddNewComment() throws JSONException {
        if (params != null) {
            Services.sendToAddNewComment(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    Log.d("sendToAddNewComment: (onResponse)", response.body());
                    if (response.isSuccessful()) {
                        if (runnable != null) runnable.run();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToAddNewComment: (onFailure)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToAddNewAnswer() throws JSONException {
        if (params != null) {
            Services.sendToAddNewAnswer(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    Log.d("sendToAddNewAnswer: (onResponse)", response.body());
                    if (response.isSuccessful()) {
                        if (runnable != null) runnable.run();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToAddNewAnswer: (onFailure)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetAllComments() {
        if (params != null) {
            Services.sendToGetAllComments(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllComments.amountOfPagination) {
                                    PagingAdapterComments.isEnd = true;
                                }

                                ((CommentsLibrary) params[3]).setDataArrayList(jsonArray);
                                if (runnable != null) runnable.run();
                            } catch (JSONException | ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (params[4] != null) ((Runnable) params[4]).run();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllComments: ", t.getMessage());
                }
            }, (int) params[0], (int) params[1], params[2].toString());
        }
    }

    @Override
    public void sendToDeleteComment() throws JSONException {
        if (params != null) {
            Services.sendToDeleteComment(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Resources.getToast(activity, activity.getString(R.string.successful_delete)).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("onFailure: (sendToDeletePost)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToChangeComment() throws JSONException {
        if (params != null) {
            Services.sendToChangeComment(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    Log.d("sendToChangeComment: (onResponse)", response.body());


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToChangeComment: (onFailure)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetPostsOfUserInCells() {
        if (params != null) {
            Services.sendToGetPostsOfUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllPostsInCellsPosts.amountOfPagination) {
                                    PagingAdapterPostsCells.isEnd = true;
                                }

                                ((PostsLibrary) params[3]).setDataArrayList(jsonArray);
                                if (runnable != null) runnable.run();
                            } catch (JSONException e) {
                                Log.d("JSONException: ", e.getMessage());
                            }
                        }

                        if (params[4] != null) ((Runnable) params[4]).run();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetPostsOfUser: ", t.getMessage());
                }
            }, (int) params[0], (int) params[1], params[2].toString());
        }
    }

    @Override
    public void sendToChangeUser() throws JSONException {
        if (params != null) {
            Services.sendToChangeUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Errors.editProfile(activity, response.body()).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToChangeUser: (onFailure) ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToSendCodeForChangeEmail() throws JSONException {
        if (params != null) {
            Services.sendToSendCodeForEmail(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Errors.forgotPasswordCode(activity, response.body()).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToSendCodeForEmail: (onFailure) ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToChangeEmailFinally() throws JSONException {
        if (params != null) {
            Services.sendToChangeEmailFinally(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        Errors.emailCodes(activity, response.body()).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToSendCodeForEmail: (onFailure) ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToFindUser() {
        if (params != null) {
            Services.sendToFindUser(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllUsers.amountOfPagination) {
                                    PagingAdapterUsers.isEnd = true;
                                }

                                ((UsersLibrary) params[1]).setDataArrayList(jsonArray);
                                if (runnable != null) runnable.run();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (params[2] != null) ((Runnable) params[2]).run();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllPosts: ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void  sendToFindPost() {
        if (params != null) {
            Services.sendToFindPost(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllPosts.amountOfPagination) {
                                    PagingAdapterPosts.isEnd = true;
                                }

                                ((PostsLibrary) params[1]).setDataArrayList(jsonArray);
                                if (runnable != null) {
                                    runnable.run();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (params[2] != null) ((Runnable) params[2]).run();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllPosts: ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetIsMeSubscribed() throws JSONException {
        if (params != null) {
            Services.sendToGetIsMeSubscribed(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        boolean isSubscribed = Boolean.parseBoolean(response.body());
                        Cache.saveSP(activity, CacheScopes.IS_SUBSCRIBED.toString(), isSubscribed);

                        if (params[1].getClass() == AppCompatButton.class)
                            ((AppCompatButton) params[1]).setText(!isSubscribed ? activity.getResources().getString(R.string.subscribe_btn) : activity.getResources().getString(R.string.unsubscribe_btn));
                        else if (params[1].getClass() == AppCompatCheckBox.class) {
                            ((AppCompatCheckBox) params[1]).setChecked(isSubscribed);
                            ((AppCompatCheckBox) params[1]).setText(!isSubscribed ? activity.getResources().getString(R.string.subscribe_btn) : activity.getResources().getString(R.string.unsubscribe_btn));
                        }
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToSendCodeForEmail: (onFailure) ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToSetStateSubscribe() throws JSONException {
        if (params != null) {
            Services.sendToSetStateOfSubscribe(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    Log.d("sendToSetStateSubscribe: (onResponse)", response.body());


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("onFailure: (onResponse)", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetSubscribers() {
        if (params != null) {
            Services.sendToGetSubscribers(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllUsers.amountOfPagination) {
                                    PagingAdapterUsers.isEnd = true;
                                }

                                ((UsersLibrary) params[1]).setDataArrayList(jsonArray);
                                if (runnable != null) runnable.run();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (params[2] != null) ((Runnable) params[2]).run();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllPosts: ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetSubscribing() {
        if (params != null) {
            Services.sendToGetSubscribing(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllUsers.amountOfPagination) {
                                    PagingAdapterUsers.isEnd = true;
                                }

                                ((UsersLibrary) params[1]).setDataArrayList(jsonArray);
                                if (runnable != null) runnable.run();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (params[2] != null) ((Runnable) params[2]).run();
                    }


                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllPosts: ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetNotifications() {
        if (params != null) {
            Services.sendToGetNotifications(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllUsers.amountOfPagination) {
                                    PagingAdapterNotifications.isEnd = true;
                                }

                                ((NotificationsLibrary) params[3]).setDataArrayList(jsonArray);
                                if (runnable != null) runnable.run();
                            } catch (JSONException | ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (params[4] != null) ((Runnable) params[4]).run();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllPosts: ", t.getMessage());
                }
            }, params[0].toString(), params[1].toString(), params[2].toString());
        }
    }

    @Override
    public void sendToGetPostById() {
        if (params != null) {
            Services.sendToGetPostById(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONObject jsonObject = new JSONObject(body);
                                PaginationViewNotifications.publicPost = new Post(jsonObject);
                                if (runnable != null) runnable.run();
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllPosts: ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void sendToGetCommentById() {
        if (params != null) {
            Services.sendToGetCommentById(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONObject jsonObject = new JSONObject(body);
                                PaginationViewNotifications.publicComment = new Comment(jsonObject);
                                if (runnable != null) runnable.run();
                            } catch (JSONException | ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllPosts: ", t.getMessage());
                }
            }, params[0].toString());
        }
    }

    @Override
    public void getSavedPosts() {
        if (params != null) {
            Services.getSavedPosts(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String body = response.body();

                        if (!body.equals("[]")) {
                            try {
                                JSONArray jsonArray = new JSONArray(body);

                                if (jsonArray.length() < PaginationCurrentForAllPostsInCellsPosts.amountOfPagination) {
                                    PagingAdapterPostsCells.isEnd = true;
                                }

                                ((PostsLibrary) params[3]).setDataArrayList(jsonArray);
                                if (runnable != null) {
                                    runnable.run();
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (params[4] != null) ((Runnable) params[4]).run();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAllPosts: ", t.getMessage());
                }
            }, params[0].toString(), params[1].toString(), params[2].toString());
        }
    }
}
