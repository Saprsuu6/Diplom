package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.instagram.R;
import com.example.instagram.main_process.CreatePost;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TagPeople {
    private final Context context;
    private final Resources resources;
    private final Activity activity;
    private int amountTags = 0;

    public TagPeople(Context context, Resources resources, Activity activity) {
        this.context = context;
        this.resources = resources;
        this.activity = activity;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public Dialog getToTagPeople() {
        TextView taggedPeople = activity.findViewById(R.id.tag_people);

        @SuppressLint("InflateParams") View view = LayoutInflater.from(context).inflate(R.layout.tag_people, null, true);
        ThemesBackgrounds.loadBackground(activity, (view));

        Button close = view.findViewById(R.id.close);
        LinearLayout general = view.findViewById(R.id.tag_people);
        ImageView add = view.findViewById(R.id.add_to_tag);

        setListeners(general, add);

        // region set first item
        View itemToTag = LayoutInflater.from(context).inflate(R.layout.tage_people_item, null, true);
        addItem(general, itemToTag);
        // endregion

        Dialog dialog = GetDialog.getDialog(activity, view);

        close.setOnClickListener(v -> {
            LinearLayout tagPeople = view.findViewById(R.id.tag_people);
            List<String> tagPeopleArray = new ArrayList<>();

            for (int i = 0; i < amountTags + 1; i++) {
                View view1 = tagPeople.getChildAt(i);
                EditText editText = view1.findViewById(R.id.nickname);
                String name = editText.getText().toString().trim();

                if (!name.equals("")) {
                    tagPeopleArray.add(name);
                }
            }

            String names = tagPeopleArray.stream().map(String::valueOf).collect(Collectors.joining(","));

            String[] namesArray = names.split(",");

            // region removing duplicates
            int duplicates = 0;
            for (int i = 0; i < namesArray.length - 1; i++) {
                if (namesArray[i].equals(namesArray[i + 1])) {
                    namesArray[i] = null;
                    duplicates++;
                }
            }

            String[] namesWithoutDuplicatesArray = new String[namesArray.length - duplicates];
            for (int i = 0; i < namesArray.length; i++) {
                if (namesArray[i] != null) {
                    namesWithoutDuplicatesArray[i] = namesArray[i];
                }
            }
            // endregion

            String namesWithoutDuplicates = String.join(" ", namesWithoutDuplicatesArray);
            com.example.instagram.services.Resources.setText(resources.getString(R.string.tag_people), taggedPeople);
            CreatePost.PostToAdd.taggedPeople = namesWithoutDuplicates;
            dialog.dismiss();
        });

        return dialog;
    }

    private void setListeners(LinearLayout general, ImageView add) {
        add.setOnClickListener(v -> {
            @SuppressLint("InflateParams") View itemToTag = LayoutInflater.from(context)
                    .inflate(R.layout.tage_people_item, null, true);

            addItem(general, itemToTag);
            amountTags++;
        });
    }

    private void addItem(LinearLayout general, View itemToTag) {
        LinearLayout tagPeopleItem = itemToTag.findViewById(R.id.tag_people_item);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        params.setMargins(10, 10, 10, 10);
        tagPeopleItem.setLayoutParams(params);

        ImageView ava = itemToTag.findViewById(R.id.ava_view);
        ImageView error = itemToTag.findViewById(R.id.validation_error);
        EditText login = itemToTag.findViewById(R.id.nickname);

        AnimationDrawable animationDrawable = (AnimationDrawable) error.getBackground();
        animationDrawable.setExitFadeDuration(4000);

        com.example.instagram.services.Resources.setHintIntoEditText(resources.getString(R.string.tag_people_item), login);
        setValidators(login, ava, error, animationDrawable);

        general.addView(itemToTag, 0);
    }

    private void setValidators(EditText login, ImageView ava, ImageView error, AnimationDrawable animationDrawable) {
        login.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                System.out.println("beforeTextChanged");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                System.out.println("onTextChanged");
            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    new DoCallBack().setValues(animationDrawable::start, activity, new Object[]{s, error, ava}).sendToGetAvaInTagPeople();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
