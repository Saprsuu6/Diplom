package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.Services;
import com.example.instagram.services.TagPeople;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePost extends AppCompatActivity {
    private Resources resources;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion

    private EditText[] editTexts;
    private TextView[] textViews;
    private ImageView[] imageViews;
    private View videoView;
    private LinearLayout createNewPost;
    private byte[] imageBytes;
    private String extension;
    private TagPeople tagPeople;
    private DatePickerDialog.OnDateSetListener setListener;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        findViews();
        setIntents();

        UiVisibility.setUiVisibility(this);

        resources = getResources();

        tagPeople = new TagPeople(this, resources);
        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        Localisation.setFirstLocale(languages);

        setListeners();
        setAnimations(createNewPost).start();

        openGallery();
    }

    private AnimationDrawable setAnimations(View view) {
        AnimationDrawable animationDrawable = (AnimationDrawable) view.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        return animationDrawable;
    }

    private void setIntents() {
        if (Intents.getNewsList() == null) {
            Intents.setNewsList(new Intent(this, NewsLine.class));
        }
    }

    private void setToTagPeople() {
        AlertDialog.Builder permissionsDialog = tagPeople.getToTagPeople();
        permissionsDialog.create().show();
    }

    private void findViews() {
        createNewPost = findViewById(R.id.create_post);
        languages = findViewById(R.id.languages);
        editTexts = new EditText[]{findViewById(R.id.new_post_description)};
        textViews = new TextView[]{findViewById(R.id.new_post_title), findViewById(R.id.tag_people),
                findViewById(R.id.add_place), findViewById(R.id.postpone_publication)};
        imageViews = new ImageView[]{findViewById(R.id.cancel), findViewById(R.id.done),
                findViewById(R.id.preview_photo), findViewById(R.id.cancel), findViewById(R.id.done)};
        videoView = findViewById(R.id.video_content);
    }

    @SuppressLint("IntentReset")
    private void openGallery() {
        if (TransitUser.user.getOtherInfo().isMediaPermission()) {
            @SuppressLint("IntentReset") Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);

            someActivityResultLauncher.launch(intent);
        } else {
            AlertDialog.Builder permissionsDialog = Permissions.getPermissionMediaDialog(this, resources);
            permissionsDialog.setNegativeButton("Cancel", null);
            permissionsDialog.create().show();
        }
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Uri selectedImageUri = data.getData();

                    Bitmap selectedImage;

                    extension = FindExtension.getExtension(selectedImageUri, getApplicationContext());

                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);

                        // region Get image bytes
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            imageBytes = imageStream.readAllBytes();
                        }
                        //endregion

                        selectedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        // region File -> byte[] of ByteArrayOutputStream
                        if (selectedImage != null) {
                            imageViews[2].setImageBitmap(selectedImage);
                        }
                        // endregion
                    } catch (IOException e) {
                        Log.d("ActivityResultLauncher & IOException: ", e.getMessage());
                    }
                }
            }
    );

    @SuppressLint("SetTextI18n")
    private void setListeners() {
        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();

                if (selectedDate != null) {
                    DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
                    textViews[3].setText(resources.getString(R.string.postpone_publication) + ": " + DateFormatting.formatDate(selectedDate));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        textViews[3].setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    CreatePost.this,
                    android.R.style.Theme_DeviceDefault_Dialog,
                    setListener, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        setListener = (view, year1, month1, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());

            if (year >= year1 && month >= month1 && day >= dayOfMonth) {
                TransitPost.post.setPostponePublication(selectedDate.getTime());
                textViews[3].setText(resources.getString(R.string.postpone_publication) + ": " + DateFormatting.formatDate(selectedDate));
            } else {
                Toast.makeText(this, R.string.birthday_error_date, Toast.LENGTH_SHORT).show();
            }
        };

        imageViews[0].setOnClickListener(v -> finish());

        imageViews[1].setOnClickListener(v -> {
            if (imageBytes.length == 0) {
                Toast.makeText(getApplicationContext(), R.string.error_no_photo, Toast.LENGTH_SHORT).show(); // TODO st localize
                return;
            }

            RequestBody image = RequestBody.create(MediaType.parse("image/" + extension), imageBytes); // TODO set extension

            try {
                Services.sendMultipartPost(new Callback<>() { // TODO change to other method to send post
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        assert response.body() != null;
                        String responseStr = response.body().toString();

                        if (responseStr.equals("0")) {
                            Toast.makeText(getApplicationContext(), R.string.successfully_loaded_0, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.unsuccessfully_loaded_1, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        Log.d("sendMultipartPost: ", t.getMessage());
                    }
                }, image, editTexts[0].getText().toString().trim());
            } catch (JSONException e) {
                Log.d("JSONException: ", e.getMessage());
            }
            // endregion

            finish();
        });

        imageViews[2].setOnClickListener(v -> openGallery());

        textViews[1].setOnClickListener(v -> setToTagPeople());
    }

    // region Localisation
    @SuppressLint("SetTextI18n")
    private void setStringResources() {
        editTexts[0].setHint(resources.getString(R.string.add_description));

        textViews[0].setText(resources.getString(R.string.new_post));
        textViews[1].setText(resources.getString(R.string.tag_people));
        textViews[2].setText(resources.getString(R.string.add_a_place));
        textViews[3].setText(resources.getString(R.string.postpone_publication));

        if (selectedDate != null) {
            textViews[3].setText(textViews[3].getText() + ": " + DateFormatting.formatDate(selectedDate));
        }
    }
    // endregion
}