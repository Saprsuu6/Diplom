package com.example.instagram.services.pagination.adapters;

import android.content.Context;
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

import org.json.JSONException;
import org.json.JSONObject;

public class PaginationViewUsers extends RecyclerView.Adapter<PaginationViewUsers.ViewHolderUser> {
    private final Context context;
    private final UsersLibrary usersLibrary;

    public UsersLibrary getUsersLibrary() {
        return usersLibrary;
    }

    public PaginationViewUsers(Context context, UsersLibrary usersLibrary) {
        this.context = context;
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

        // region send request to get avatar
        try {
            new DoCallBack().setValues(null, context, new Object[]{data.getLogin(), holder.avaView}).sendToGetAvaImage();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        // endregion

        // set name nickname
        holder.loginView.setText(data.getLogin());
        holder.nameView.setText(data.getNickName());

        String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());
        if (!data.getLogin().equals(login)) {
            try {
                JSONObject jsonObject = User.getJSONToKnowIsMeSubscribed(login, data.getLogin());
                new DoCallBack().setValues(null, context, new Object[]{jsonObject, holder.subscribe}).sendToGetIsMeSubscribed();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        } else {
            holder.subscribe.setVisibility(View.GONE);
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
                subscribe.setText(!subscribe.isChecked() ? context.getString(R.string.subscribe_btn) : context.getString(R.string.unsubscribe_btn));
                String login = Cache.loadStringSP(context, CacheScopes.USER_LOGIN.toString());

                try {
                    JSONObject jsonObject = User.getJSONToSubscribe(login, loginView.getText().toString(), subscribe.isChecked());
                    jsonObject.put("token", Cache.loadStringSP(context, CacheScopes.USER_TOKEN.toString()));
                    new DoCallBack().setValues(null, context, new Object[]{jsonObject}).sendToSetStateSubscribe();
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
                        context.startActivity(Intents.getSelfPage());
                    }, context, new Object[]{loginView.getText().toString()}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            loginView.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{loginView}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            nameView.setOnClickListener(v -> {
                try {
                    new DoCallBack().setValues(() -> context.startActivity(Intents.getSelfPage()), context, new Object[]{loginView}).sendToGetCurrentUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}