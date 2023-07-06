package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.R;
import com.example.instagram.main_process.UserPage;
import com.example.instagram.services.pagination.paging_views.PagingAdapterUsers;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;
import org.json.JSONObject;

public class FindUser {
    private final Context context;
    private final Activity activity;
    private final View view;
    private EditText search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PagingAdapterUsers pagingViewUsers;
    private Boolean isSubscribers;

    @SuppressLint("InflateParams")
    public FindUser(Context context, Activity activity) throws JSONException {
        this.context = context;
        this.activity = activity;

        view = LayoutInflater.from(context).inflate(R.layout.search_users, null, true);
    }

    @SuppressLint("InflateParams")
    public AlertDialog.Builder getToFindUser(@Nullable Boolean isSubscribers) throws JSONException {
        this.isSubscribers = isSubscribers;

        ThemesBackgrounds.loadBackground(activity, ((LinearLayout) view));
        search = view.findViewById(R.id.nickname);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        search.setHint(context.getString(R.string.find_user));

        setListeners();

        if (isSubscribers == null){
            showAgain();
        }

        return new AlertDialog.Builder(context).setCancelable(false).setNegativeButton(context.getString(R.string.close), (dialog, which) -> closeDialog(dialog)).setView(view);
    }

    private void closeDialog(DialogInterface dialog) {
        ViewGroup vGroup = (ViewGroup) view.getParent();
        vGroup.removeView(view);

        dialog.cancel();
        dialog.dismiss();
    }

    private void showAgain() throws JSONException {
        if (pagingViewUsers != null) {
            pagingViewUsers.notifyAdapterToClearAll();
        } else {
            JSONObject bodyJSON = getJSONToFind();
            pagingViewUsers = new PagingAdapterUsers(view.findViewById(R.id.scroll_view), view.findViewById(R.id.recycler_view), view.findViewById(R.id.skeleton), activity.getApplicationContext(), activity, bodyJSON, isSubscribers);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private JSONObject getJSONToFind() throws JSONException {
        JSONObject bodyJSON;
        JSONObject paramsJSON;

        // TODO send unnecessary param selfPageUserLogin

        paramsJSON = new JSONObject();

        paramsJSON.put("login", search.getText());

        bodyJSON = new JSONObject();
        bodyJSON.put("params", paramsJSON);

        return bodyJSON;
    }

    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            try {
                showAgain();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        if (isSubscribers == null) {
            search.setVisibility(View.VISIBLE);

            search.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    System.out.println("before");
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    System.out.println("on");
                }

                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        JSONObject bodyJSON = getJSONToFind();
                        pagingViewUsers = new PagingAdapterUsers(view.findViewById(R.id.scroll_view), view.findViewById(R.id.recycler_view), view.findViewById(R.id.skeleton), activity.getApplicationContext(), activity, bodyJSON, isSubscribers);
                    } catch (JSONException e) {
                        Log.d("JSONException: ", e.getMessage());
                    }
                }
            });
        } else {
            search.setVisibility(View.GONE);

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("login", UserPage.userPage.getLogin());
                pagingViewUsers = new PagingAdapterUsers(view.findViewById(R.id.scroll_view), view.findViewById(R.id.recycler_view), view.findViewById(R.id.skeleton), activity.getApplicationContext(), activity, jsonObject, isSubscribers);
            } catch (JSONException e) {
                Log.d("JSONException: ", e.getMessage());
            }
        }
    }

}
