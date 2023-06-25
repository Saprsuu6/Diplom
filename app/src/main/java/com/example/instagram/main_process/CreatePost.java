package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.DAOs.Post;
import com.example.instagram.R;
import com.example.instagram.services.Animation;
import com.example.instagram.services.AudioController;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.MediaTypes;
import com.example.instagram.services.OpenMedia;
import com.example.instagram.services.ReadBytesForMedia;
import com.example.instagram.services.Services;
import com.example.instagram.services.TagPeople;
import com.example.instagram.services.TransitPost;
import com.example.instagram.services.UiVisibility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePost extends AppCompatActivity {
    private AudioController audioController;
    private Resources resources;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion

    private EditText[] editTexts;
    private TextView[] textViews;
    private ImageView[] imageViews;
    private Button[] buttons;
    private VideoView videoView;
    private LinearLayout audioControllerLinearLayout;
    private LinearLayout createNewPost;
    private byte[] mediaBytes;
    private String extension;
    private TagPeople tagPeople;
    private DatePickerDialog.OnDateSetListener setListener;
    private SeekBar seekBar;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        findViews();
        setIntents();

        UiVisibility.setUiVisibility(this);

        resources = getResources();

        tagPeople = new TagPeople(this, resources, this);
        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        setListeners();
        Animation.getAnimations(createNewPost).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(languages);
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
        audioControllerLinearLayout = findViewById(R.id.audio_controller);
        editTexts = new EditText[]{findViewById(R.id.new_post_description)};
        textViews = new TextView[]{findViewById(R.id.new_post_title), findViewById(R.id.tag_people), findViewById(R.id.add_place), findViewById(R.id.postpone_publication), findViewById(R.id.time_line)};
        imageViews = new ImageView[]{findViewById(R.id.cancel), findViewById(R.id.done), findViewById(R.id.preview_photo), findViewById(R.id.cancel), findViewById(R.id.done), findViewById(R.id.play_stop), findViewById(R.id.play_prev), findViewById(R.id.play_next)};
        videoView = findViewById(R.id.video_content);
        buttons = new Button[]{findViewById(R.id.btn_video), findViewById(R.id.btn_image), findViewById(R.id.btn_audio)};
        seekBar = findViewById(R.id.seek_bar);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();

            if (data != null) {
                Uri selectedUri = data.getData();

                extension = FindExtension.getExtension(selectedUri, getApplicationContext());
                String mime = Post.getMimeTypeFromExtension(extension);
                Bitmap selectedImage;

                getFileName(selectedUri);

                try {
                    mediaBytes = ReadBytesForMedia.readBytes(this, selectedUri);

                    if (mediaBytes == null) {
                        Toast.makeText(this, getString(R.string.tiramisu_or_better), Toast.LENGTH_SHORT).show();
                    }

                    JSONObject object = new JSONObject();
                    object.put("Extension", extension);

                    if (mime.contains(getString(R.string.mime_image))) {
                        //set image
                        selectedImage = BitmapFactory.decodeByteArray(mediaBytes, 0, mediaBytes.length);

                        if (selectedImage != null) {
                            imageViews[2].setVisibility(View.VISIBLE);
                            //imageViews[2].setImageBitmap(selectedImage);
                            imageViews[2].setImageURI(selectedUri);

                            object.put("Size", selectedImage.getHeight() + "x" + selectedImage.getWidth());
                            TransitPost.post.setMetadata(object.toString());
                        }
                    } else if (mime.contains(getString(R.string.mime_video))) {
                        // set video
                        videoView.setVisibility(View.VISIBLE);
                        videoView.setVideoURI(selectedUri);
                        videoView.start();
                        videoView.requestFocus();

                        object.put("Size", videoView.getHeight() + "x" + videoView.getWidth());
                        TransitPost.post.setMetadata(object.toString());
                    } else if (mime.contains(getString(R.string.mime_audio))) {
                        // set audio
                        audioControllerLinearLayout.setVisibility(View.VISIBLE);
                        audioController = new AudioController(textViews[4], seekBar, imageViews[5], imageViews[6], imageViews[7], getApplicationContext(), selectedUri);
                        audioController.initHandler(new Handler());
                        object.put("Duration", audioController.getDuration());
                        TransitPost.post.setMetadata(object.toString());
                    }
                    // endregion
                } catch (IOException e) {
                    Log.d("ActivityResultLauncher & IOException: ", e.getMessage());
                } catch (JSONException e) {
                    Log.d("JSONException (setMetadata): ", e.getMessage());
                }
            }
        }
    });

    @SuppressLint("Range")
    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                assert cursor != null;
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private void goneButtons() {
        buttons[0].setVisibility(View.GONE);
        buttons[1].setVisibility(View.GONE);
        buttons[2].setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void setListeners() {
        buttons[0].setOnClickListener(v -> {
            OpenMedia.openGallery(this, MediaTypes.VIDEO, someActivityResultLauncher);
            goneButtons();
        });

        buttons[1].setOnClickListener(v -> {
            OpenMedia.openGallery(this, MediaTypes.IMAGE, someActivityResultLauncher);
            goneButtons();
        });

        buttons[2].setOnClickListener(v -> {
            OpenMedia.openGallery(this, MediaTypes.AUDIO, someActivityResultLauncher);
            goneButtons();
        });

        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();

                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());

                if (selectedDate != null) {
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
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDate.set(Calendar.MINUTE, minute);

            TransitPost.post.setPostponePublication(selectedDate.getTime());
        };

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year1, month1, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            //DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());

            if (year >= year1 && month >= month1 && dayOfMonth >= day) {
                textViews[3].setText(resources.getString(R.string.postpone_publication) + ": " + DateFormatting.formatDate(selectedDate));
            } else {
                Toast.makeText(this, R.string.birthday_error_date, Toast.LENGTH_SHORT).show();
            }
        };

        textViews[3].setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreatePost.this, android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);
            TimePickerDialog timePickerDialog = new TimePickerDialog(CreatePost.this, android.R.style.Theme_DeviceDefault_Dialog, timeSetListener, hours, minutes, !Localisation.chosenLocale.getCountry().equals("EN"));

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            timePickerDialog.show();
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        imageViews[0].setOnClickListener(v -> finish());

        imageViews[1].setOnClickListener(v -> {
            if (mediaBytes == null || mediaBytes.length == 0) {
                Toast.makeText(getApplicationContext(), R.string.error_no_photo, Toast.LENGTH_SHORT).show(); // TODO st localize
                audioController.clearHandler();
                finish();
                return;
            }

            RequestBody media = null;
            String mime = Post.getMimeTypeFromExtension(extension);

            if (mime.contains(getString(R.string.mime_image))) {
                media = RequestBody.create(MediaType.parse(getString(R.string.mime_image) + "/" + extension), mediaBytes);
            } else if (mime.contains(getString(R.string.mime_video))) {
                media = RequestBody.create(MediaType.parse(getString(R.string.mime_video) + "/" + extension), mediaBytes);
            } else if (mime.contains(getString(R.string.mime_audio))) {
                media = RequestBody.create(MediaType.parse(getString(R.string.mime_audio) + "/" + extension), mediaBytes);
            }

            TransitPost.post.setDescription(editTexts[0].getText().toString());

            try {
                String otherInfo = TransitPost.post.crateOtherInfo().toString();

                Services.sendMultipartPost(new Callback<>() {
                    @Override
                    public void onResponse(@NotNull Call<ResponseBody> call, @NotNull Response<ResponseBody> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(getApplicationContext(), R.string.successfully_loaded_0, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NotNull Call<ResponseBody> call, @NotNull Throwable t) {
                        Log.d("sendMultipartPost: ", t.getMessage());
                    }
                }, media, otherInfo);
            } catch (JSONException e) {
                Log.d("JSONException: ", e.getMessage());
            }
            // endregion

            if (audioController != null) audioController.clearHandler();
            finish();
        });

        imageViews[2].setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.IMAGE, someActivityResultLauncher));
        videoView.setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.VIDEO, someActivityResultLauncher));

        textViews[1].setOnClickListener(v -> setToTagPeople());
    }

    // region Localisation
    @SuppressLint("SetTextI18n")
    private void setStringResources() {
        editTexts[0].setHint(resources.getString(R.string.add_description));

        textViews[0].setText(resources.getString(R.string.new_post));

        if (TransitPost.post.getNickNames() != null) {
            textViews[1].setText(resources.getString(R.string.tag_people) + ": " + TransitPost.post.getNickNames());
        }

        textViews[2].setText(resources.getString(R.string.add_a_place));
        textViews[3].setText(resources.getString(R.string.postpone_publication));

        if (selectedDate != null) {
            textViews[3].setText(textViews[3].getText() + ": " + DateFormatting.formatDate(selectedDate));
        }
    }
    // endregion
}