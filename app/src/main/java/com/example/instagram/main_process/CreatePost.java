package com.example.instagram.main_process;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.authentication.ForgotPassword;
import com.example.instagram.authentication.Registration;
import com.example.instagram.services.CreatePhotoFile;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Multipart;
import com.example.instagram.services.MyRetrofit;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
    private File file;

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
            intent.setType("image/jpeg");
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

                    try {
                        InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);
                        selectedImage = BitmapFactory.decodeStream(imageStream);

//                        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//                        selectedImage.compress(Bitmap.CompressFormat.JPEG, 0, bos);
//                        byte[] bitmapData = bos.toByteArray();

                        // region File -> byte[] of ByteArrayOutputStream
                        if (selectedImage != null) {
                            file = CreatePhotoFile.createFile(selectedImage, getApplicationContext());
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
            // region Send file to back-end
            RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);
            MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

            try {
                Services.sendNewPost(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@Nullable Call<ResponseBody> call, @Nullable Response<ResponseBody> response) {
                        // region test response
                        byte[] bytes;

                        try {
                            bytes = response.body().bytes();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        File myObj = null;
                        try {
                            myObj = File.createTempFile("image", "jpg");
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        try (FileOutputStream outputStream = new FileOutputStream(myObj)) {
                            outputStream.write(bytes);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        InputStream inputStream = null;

                        try {
                            inputStream = new FileInputStream(myObj);
                        } catch (FileNotFoundException e) {
                            throw new RuntimeException(e);
                        }

                        StringBuilder resultStringBuilder = new StringBuilder();
                        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                        String line;
                        while (true) {
                            try {
                                if (!((line = br.readLine()) != null)) break;
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            resultStringBuilder.append(line);
                        }

                        byte[] output3 = resultStringBuilder.toString().getBytes(StandardCharsets.UTF_8);


                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inMutable = true;
                        Bitmap bmp = BitmapFactory.decodeByteArray(output3, 0, output3.length, options);
                        Canvas canvas = new Canvas(bmp); // now it should work ok
                        // endregion

                        System.out.println(response.body());
                    }

                    @Override
                    public void onFailure(@Nullable Call<ResponseBody> call, @Nullable Throwable t) {
                        assert t != null;
                        System.out.println(t.getMessage());
                    }
                }, body);
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