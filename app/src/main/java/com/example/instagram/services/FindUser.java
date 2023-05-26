package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;

import com.example.instagram.R;
import com.example.instagram.services.pagination.paging_views.PagingViewFindUsers;

import org.json.JSONException;
import org.json.JSONObject;

public class FindUser {
    private final Context context;
    private final Resources resources;
    private final View view;
    private PagingViewFindUsers pagingViewUsers;

    @SuppressLint("InflateParams")
    public FindUser(Context context, Resources resources) throws JSONException {
        this.context = context;
        this.resources = resources;

        view = LayoutInflater.from(context)
                .inflate(R.layout.search_users, null, true);
    }

    @SuppressLint("InflateParams")
    public AlertDialog.Builder getToFindUser() {
        //LinearLayout general = view.findViewById(R.id.search_user);
        EditText editText = view.findViewById(R.id.nickname);
        editText.setHint(resources.getString(R.string.find_user));

        setListeners(editText);

        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setNegativeButton(resources.getString(R.string.close), (dialog, which) -> closeDialog(dialog))
                .setView(view)
                .setPositiveButton(resources.getString(R.string.permission_ok), (dialog, which) ->
                        closeDialog(dialog)
                );
    }

    private void closeDialog(DialogInterface dialog) {
        ViewGroup vGroup = (ViewGroup) view.getParent();
        vGroup.removeView(view);

        dialog.cancel();
        dialog.dismiss();
    }

    private void setListeners(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
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
                JSONObject nickNameJSON;

                try {
                    nickNameJSON = new JSONObject();
                    nickNameJSON.put("nickName", s.toString());
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }

                try {
                    pagingViewUsers = new PagingViewFindUsers(view.findViewById(R.id.scroll_view),
                            view.findViewById(R.id.recycler_view), view.findViewById(R.id.skeleton),
                            context, null, 1, 20);
                } catch (JSONException e) {
                    System.out.println(e.getMessage());
                }

                // TODO: send request to server
            }
        });
    }
}
