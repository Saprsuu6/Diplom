package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.instagram.R;
import com.example.instagram.services.pagination.PaginationCurrentForAllPosts;
import com.example.instagram.services.pagination.PaginationCurrentForAllUsers;
import com.example.instagram.services.pagination.paging_views.PagingViewFindUsers;
import com.example.instagram.services.pagination.paging_views.PagingViewGetAllPosts;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FindUser {
    private final Context context;
    private final Activity activity;
    private final View view;
    private EditText search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private PagingViewFindUsers pagingViewUsers;

    @SuppressLint("InflateParams")
    public FindUser(Context context, Activity activity) throws JSONException {
        this.context = context;
        this.activity = activity;

        view = LayoutInflater.from(context).inflate(R.layout.search_users, null, true);
    }

    @SuppressLint("InflateParams")
    public AlertDialog.Builder getToFindUser() throws JSONException {
        search = view.findViewById(R.id.nickname);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        search.setHint(context.getString(R.string.find_user));

        setListeners();
        showAgain();

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
            pagingViewUsers = new PagingViewFindUsers(view.findViewById(R.id.scroll_view), view.findViewById(R.id.recycler_view), view.findViewById(R.id.skeleton), activity.getApplicationContext(), activity, bodyJSON);
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private JSONObject getJSONToFind() throws JSONException {
        JSONObject bodyJSON;
        JSONObject paramsJSON;

        paramsJSON = new JSONObject();
        paramsJSON.put("login", search.getText());
        // TODO or
        // paramsJSON.put("name", search.getText());

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
                    pagingViewUsers = new PagingViewFindUsers(view.findViewById(R.id.scroll_view), view.findViewById(R.id.recycler_view), view.findViewById(R.id.skeleton), activity.getApplicationContext(), activity, bodyJSON);
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            }
        });
    }

}
