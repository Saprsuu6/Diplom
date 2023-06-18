package com.example.instagram.services;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.TooltipCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.R;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TagPeople {
    private final Context context;
    private final Resources resources;
    private final Activity activity;

    public TagPeople(Context context, Resources resources, Activity activity) {
        this.context = context;
        this.resources = resources;
        this.activity = activity;
    }

    @SuppressLint({"InflateParams", "SetTextI18n"})
    public AlertDialog.Builder getToTagPeople() {
        TextView taggedPeople = activity.findViewById(R.id.tag_people);

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
                .setPositiveButton(resources.getString(R.string.permission_ok), (dialog, which) -> {
                    LinearLayout tagPeople = view.findViewById(R.id.tag_people);
                    int childCount = tagPeople.getChildCount();
                    List<String> tagPeopleArray = new ArrayList<>();

                    for (int i = 0; i < childCount - 1; i++) {
                        View view1 = tagPeople.getChildAt(i);
                        EditText editText = view1.findViewById(R.id.nickname);
                        String name = editText.getText().toString().trim();

                        if (!name.equals("")) {
                            tagPeopleArray.add(name);
                        }
                    }

                    String names = tagPeopleArray.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));

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
                    taggedPeople.setText(resources.getString(R.string.tag_people) + ": " + namesWithoutDuplicates);

                    TransitPost.post.setNickNames(namesWithoutDuplicates);
                });
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

        ImageView ava = itemToTag.findViewById(R.id.ava_view);
        ImageView error = itemToTag.findViewById(R.id.validation_error);
        EditText login = itemToTag.findViewById(R.id.nickname);

        AnimationDrawable animationDrawable = (AnimationDrawable) error.getBackground();
        animationDrawable.setExitFadeDuration(4000);

        login.setHint(resources.getString(R.string.tag_people_item));

        setValidators(login, ava, error, animationDrawable);

        general.addView(itemToTag, general.getChildCount() - 1);
    }

    private void setValidators(EditText login, ImageView ava, ImageView error, AnimationDrawable animationDrawable) {
        login.addTextChangedListener(new TextWatcher() {
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
                try {
                    Services.sendToGetAva(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.body() != null) {
                                String avaLink = response.body();

                                if (avaLink.equals("")) {
                                    error.setVisibility(View.VISIBLE);
                                    ava.setVisibility(View.GONE);
                                    animationDrawable.start();

                                    TooltipCompat.setTooltipText(error, resources.getString(R.string.user_are_not_exists));
                                } else {
                                    // set ava
                                    Glide.with(context).load(Services.BASE_URL + avaLink)
                                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                                            .into(ava);

                                    error.setVisibility(View.GONE);
                                    ava.setVisibility(View.VISIBLE);
                                    animationDrawable.stop();
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToGetAva: ", t.getMessage());
                        }
                    }, s.toString());
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
