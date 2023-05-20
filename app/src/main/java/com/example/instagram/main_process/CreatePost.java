package com.example.instagram.main_process;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.instagram.R;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        findViews();
        setIntents();

        UiVisibility.setUiVisibility(this);

        resources = getResources();

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

                        //selectedImage = BitmapFactory.decodeStream(imageStream);
                        selectedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                        // region File -> byte[] of ByteArrayOutputStream
                        if (selectedImage != null) {
                            imageViews[2].setImageBitmap(selectedImage);
                        }
                        // endregion
                    } catch (IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            }
    );

    private void setListeners() {
        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        imageViews[0].setOnClickListener(v -> {
            finish();
        });
        imageViews[1].setOnClickListener(v -> {
            if (imageBytes.length == 0) {
                Toast.makeText(getApplicationContext(), R.string.error_no_photo, Toast.LENGTH_SHORT).show(); // TODO st localize
                return;
            }

            RequestBody image = RequestBody.create(MediaType.parse("image/" + extension), imageBytes); // TODO set extension

            try {
                Services.sendAva(new Callback<>() { // TODO change to other method to send post
                    @Override
                    public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                        assert Objects.requireNonNull(response).body() != null;
                        String responseStr = response.body().toString();

                        if (responseStr.equals("0")) {
                            Toast.makeText(getApplicationContext(), R.string.successfully_loaded_0, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), R.string.unsuccessfully_loaded_1, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@Nullable Call<ResponseBody> call, @Nullable Throwable t) {
                        assert t != null;
                        System.out.println(t.getMessage());
                    }
                }, image);
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
            // endregion

            // TODO decide how to send post info

            finish();
        });

        imageViews[2].setOnClickListener(v -> {
            openGallery();
        });

        // TODO add ability to tag people
        // TODO add ability to add place
        // TODO add ability to postpone post
    }

    // region Localisation
    private void setStringResources() {
        editTexts[0].setHint(resources.getString(R.string.add_description));

        textViews[0].setText(resources.getString(R.string.new_post));
        textViews[1].setText(resources.getString(R.string.tag_people));
        textViews[2].setText(resources.getString(R.string.add_a_place));
        textViews[3].setText(resources.getString(R.string.postpone_publication));
    }
    // endregion
}