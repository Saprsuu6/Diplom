package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.instagram.R;

public class TagPeople {
    private final Context context;
    private final Resources resources;

    public TagPeople(Context context, Resources resources) {
        this.context = context;
        this.resources = resources;
    }

    @SuppressLint("InflateParams")
    public AlertDialog.Builder getToTagPeople() {
        @SuppressLint("InflateParams") View view = LayoutInflater.from(context)
                .inflate(R.layout.tag_people, null, true);

        LinearLayout general = view.findViewById(R.id.tag_people);
        ImageView add = view.findViewById(R.id.add_to_tag);

        setListeners(general, add);

        // region set first item
        View itemToTag = LayoutInflater.from(context)
                .inflate(R.layout.tage_people_item, null, true);
        addItem(general, itemToTag);
        // endregion

        return new AlertDialog.Builder(context)
                .setCancelable(false)
                .setNegativeButton(resources.getString(R.string.close), null)
                .setView(view)
                .setPositiveButton(resources.getString(R.string.permission_ok), null);
    }

    private void setListeners(LinearLayout general, ImageView add) {
        add.setOnClickListener(v -> {
            @SuppressLint("InflateParams") View itemToTag = LayoutInflater.from(context)
                    .inflate(R.layout.tage_people_item, null, true);

            addItem(general, itemToTag);
        });
    }

    private void addItem(LinearLayout general, View itemToTag) {
        LinearLayout tagPeopleItem = itemToTag.findViewById(R.id.tag_people_item);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(10, 10, 10, 10);
        tagPeopleItem.setLayoutParams(params);

        EditText editText = itemToTag.findViewById(R.id.nickname);
        editText.setHint(resources.getString(R.string.tag_people_item));

        setValidators(editText);

        general.addView(itemToTag, general.getChildCount() - 1);
    }

    private void setValidators(EditText editText) {
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("beforeTextChanged"); // TODO send requests
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("onTextChanged"); // TODO send requests
            }

            @Override
            public void afterTextChanged(Editable s) {
                System.out.println("afterTextChanged"); // TODO send requests
            }
        });
    }
}
