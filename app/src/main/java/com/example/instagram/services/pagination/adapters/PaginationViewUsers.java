package com.example.instagram.services.pagination.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.instagram.DAOs.User;
import com.example.instagram.DAOs.UsersLibrary;
import com.example.instagram.R;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Resources;
import com.example.instagram.services.SetImagesGlide;

import org.json.JSONException;
import org.json.JSONObject;

public class PaginationViewUsers extends RecyclerView.Adapter<PaginationViewUsers.ViewHolderUser> {
    private final Activity activity;
    private final UsersLibrary usersLibrary;

    public UsersLibrary getUsersLibrary() {
        return usersLibrary;
    }

    public PaginationViewUsers(Activity activity, UsersLibrary usersLibrary) {
        this.activity = activity;
        this.usersLibrary = usersLibrary;
    }

    @NonNull
    @Override
    public PaginationViewUsers.ViewHolderUser onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_user, parent, false); // don't forget to change
        return new ViewHolderUser(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PaginationViewUsers.ViewHolderUser holder, int position) {
        User data = usersLibrary.getDataArrayList().get(position);

        // cache
        String userLogin = Cache.loadStringSP(activity, data.getLogin());
        Cache.saveSP(activity, data.getLogin(), data.getLogin());

        // region send request to get avatar
        try {
            new DoCallBack().setValues(null, activity, new Object[]{data.getLogin(), holder.avaView, data.getLogin()}).sendToGetAvaImage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // endregion

        // set name nickname
        Resources.setText(data.getLogin(), holder.loginView);
        Resources.setText(data.getNickName(), holder.nameView);

        String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());
        if (!data.getLogin().equals(login)) {
            if (!userLogin.equals("")) {
                boolean isSubscribed = Cache.loadBoolSP(activity, data.getLogin() + "." + "isSubscribed");
                holder.subscribe.setChecked(isSubscribed);
                Resources.setText(!isSubscribed ? activity.getResources().getString(R.string.subscribe_btn) : activity.getResources().getString(R.string.unsubscribe_btn), holder.subscribe);
            } else {
                try {
                    JSONObject jsonObject = User.getJSONToKnowIsMeSubscribed(login, data.getLogin());
                    new DoCallBack().setValues(null, activity, new Object[]{jsonObject, holder.subscribe, data.getLogin()}).sendToGetIsMeSubscribed();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            Resources.setVisibility(View.GONE, holder.subscribe);
        }
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
                Resources.setText(!subscribe.isChecked() ? activity.getString(R.string.subscribe_btn) : activity.getString(R.string.unsubscribe_btn), subscribe);
                String login = Cache.loadStringSP(activity, CacheScopes.USER_LOGIN.toString());
                Cache.saveSP(activity, loginView.getText() + "." + "isSubscribed", subscribe.isChecked());

                try {
                    JSONObject jsonObject = User.getJSONToSubscribe(login, loginView.getText().toString(), subscribe.isChecked());
                    jsonObject.put("token", Cache.loadStringSP(activity, CacheScopes.USER_TOKEN.toString()));
                    new DoCallBack().setValues(null, activity, new Object[]{jsonObject}).sendToSetStateSubscribe();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });

            setListeners();
        }

        private void setListeners() {
            avaView.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> {
                        Intents.getSelfPage().setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        activity.startActivity(Intents.getSelfPage());
                    }, activity, new Object[]{loginView.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            loginView.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{loginView}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            nameView.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> activity.startActivity(Intents.getSelfPage()), activity, new Object[]{loginView}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}