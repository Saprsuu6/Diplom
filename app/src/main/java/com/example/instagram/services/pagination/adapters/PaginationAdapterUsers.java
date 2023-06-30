package com.example.instagram.services.pagination.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
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
import com.example.instagram.DAOs.User;
import com.example.instagram.DAOs.UsersLibrary;
import com.example.instagram.R;
import com.example.instagram.main_process.SelfPage;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Services;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaginationAdapterUsers extends RecyclerView.Adapter<PaginationAdapterUsers.ViewHolderUser> {
    private final Context context;
    private final UsersLibrary usersLibrary;

    public UsersLibrary getUsersLibrary() {
        return usersLibrary;
    }

    public PaginationAdapterUsers(Context context, UsersLibrary usersLibrary) {
        this.context = context;
        this.usersLibrary = usersLibrary;
    }

    @NonNull
    @Override
    public PaginationAdapterUsers.ViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false); // don't forget to change
        return new ViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaginationAdapterUsers.ViewHolderUser holder, int position) {
        User data = usersLibrary.getDataArrayList().get(position);

        // region send request to get avatar
        try {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avaLink = response.body();
                        String imagePath = Services.BASE_URL + context.getString(R.string.root_folder) + avaLink;
                        Glide.with(context).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(holder.avaView);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, data.getLogin());
        } catch (JSONException e) {
            Log.d("sendToGetAva: (JSONException)", e.getMessage());
        }
        // endregion

        // set name nickname
        holder.loginView.setText(data.getLogin());
        holder.nameView.setText(data.getNickName());

        // set button
        holder.subscribe.setText(!holder.subscribe.isChecked() ? context.getString(R.string.subscribe_btn) : context.getString(R.string.unsubscribe_btn));
    }

    @Override
    public int getItemCount() {
        return usersLibrary.getDataArrayList().size();
    }

    public class ViewHolderUser extends RecyclerView.ViewHolder {
        private final ImageView avaView;
        private final TextView loginView;
        private final TextView nameView;
        private final CheckBox subscribe;

        public ViewHolderUser(@NonNull View itemView) {
            super(itemView);

            avaView = itemView.findViewById(R.id.ava_view);
            loginView = itemView.findViewById(R.id.nick_view);
            nameView = itemView.findViewById(R.id.name_view);
            subscribe = itemView.findViewById(R.id.btn_subscribe);

            subscribe.setOnClickListener(v -> {
                subscribe.setChecked(subscribe.isChecked());
                subscribe.setText(!subscribe.isChecked() ? context.getString(R.string.subscribe_btn) : context.getString(R.string.unsubscribe_btn));
            });

            setListeners();
        }

        private void setListeners() {
            avaView.setOnClickListener(v -> {
                try {
                    Services.sendToGetCurrentUser(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject user;

                                try {
                                    user = new JSONObject(response.body());

                                    SelfPage.userPage = User.getPublicUser(user, loginView.getText().toString());
                                    Intents.getSelfPage().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    context.startActivity(Intents.getSelfPage());
                                } catch (JSONException | ParseException e) {
                                    Log.d("JSONException", e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToGetCurrentUser: (onFailure)", t.getMessage());
                        }
                    }, loginView.getText().toString());
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            });
            loginView.setOnClickListener(v -> {
                try {
                    Services.sendToGetCurrentUser(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject user;

                                try {
                                    user = new JSONObject(response.body());

                                    SelfPage.userPage = User.getPublicUser(user, loginView.getText().toString());
                                    context.startActivity(Intents.getSelfPage());
                                } catch (JSONException | ParseException e) {
                                    Log.d("JSONException", e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToGetCurrentUser: (onFailure)", t.getMessage());
                        }
                    }, loginView.getText().toString());
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            });
            nameView.setOnClickListener(v -> {
                try {
                    Services.sendToGetCurrentUser(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                JSONObject user;

                                try {
                                    user = new JSONObject(response.body());

                                    SelfPage.userPage = User.getPublicUser(user, loginView.getText().toString());
                                    context.startActivity(Intents.getSelfPage());
                                } catch (JSONException | ParseException e) {
                                    Log.d("JSONException", e.getMessage());
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToGetCurrentUser: (onFailure)", t.getMessage());
                        }
                    }, loginView.getText().toString());
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            });
        }
    }
}