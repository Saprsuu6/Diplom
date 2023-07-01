package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.instagram.services.AudioController;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.MediaTypes;
import com.example.instagram.services.OpenMedia;
import com.example.instagram.services.ReadBytesForMedia;
import com.example.instagram.services.Services;
import com.example.instagram.services.TagPeople;
import com.example.instagram.services.UiVisibility;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePost extends AppCompatActivity {
    private class Views {
        private final LinearLayout audioControllerLinearLayout;
        private final Button btnVideo;
        private final Button btnImage;
        private final Button btnAudio;
        private final ImageView close;
        private final ImageView done;
        private final ImageView playPrev;
        private final ImageView playNext;
        private final ImageView playStop;
        private final TextView timeLine;
        private final TextView title;
        private final TextView tagPeople;
        private final TextView postponePublication;
        private final EditText description;
        private final Spinner languagesSpinner;
        private final SeekBar seekBar;
        private final VideoView videoView;
        private final ImageView photoView;

        public Views() {
            audioControllerLinearLayout = findViewById(R.id.audio_controller);
            timeLine = findViewById(R.id.time_line);
            playPrev = findViewById(R.id.play_prev);
            playNext = findViewById(R.id.play_next);
            playStop = findViewById(R.id.play_stop);
            btnVideo = findViewById(R.id.btn_video);
            btnAudio = findViewById(R.id.btn_audio);
            btnImage = findViewById(R.id.btn_image);
            close = findViewById(R.id.close);
            done = findViewById(R.id.done);
            title = findViewById(R.id.new_post_title);
            tagPeople = findViewById(R.id.tag_people);
            postponePublication = findViewById(R.id.postpone_publication);
            description = findViewById(R.id.new_post_description);
            languagesSpinner = findViewById(R.id.languages);
            seekBar = findViewById(R.id.seek_bar);
            videoView = findViewById(R.id.video_content);
            photoView = findViewById(R.id.preview_photo);
        }
    }

    public static class PostToAdd {
        public static String metadata;
        public static Date postponePublication;
        public static String description;
        public static String taggedPeople;
    }

    private AudioController audioController;
    private Resources resources;
    private Localisation localisation;
    private String extension;
    private TagPeople tagPeople;
    private Calendar selectedDate;
    private Views views;
    private byte[] mediaBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        resources = getResources();
        views = new Views();
        tagPeople = new TagPeople(this, resources, this);
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
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

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();

            if (data != null) {
                Uri selectedUri = data.getData();

                extension = FindExtension.getExtension(selectedUri, getApplicationContext());
                String mime = Post.getMimeTypeFromExtension(extension);
                Bitmap selectedImage;

                try {
                    mediaBytes = ReadBytesForMedia.readBytes(this, selectedUri);

                    if (mediaBytes == null) {
                        Toast.makeText(this, getString(R.string.tiramisu_or_better), Toast.LENGTH_SHORT).show();
                    }

                    JSONObject metadata = new JSONObject();
                    metadata.put("Extension", extension);

                    if (mime.contains(getString(R.string.mime_image))) {
                        //set image
                        selectedImage = BitmapFactory.decodeByteArray(mediaBytes, 0, mediaBytes.length);

                        if (selectedImage != null) {
                            views.photoView.setVisibility(View.VISIBLE);
                            views.photoView.setImageURI(selectedUri);

                            metadata.put("Size", selectedImage.getHeight() + "x" + selectedImage.getWidth());
                        }
                    } else if (mime.contains(getString(R.string.mime_video))) {
                        // set video
                        views.videoView.setVisibility(View.VISIBLE);
                        views.videoView.setVideoURI(selectedUri);
                        views.videoView.start();
                        views.videoView.requestFocus();

                        metadata.put("Size", views.videoView.getHeight() + "x" + views.videoView.getWidth());
                    } else if (mime.contains(getString(R.string.mime_audio))) {
                        // set audio
                        views.audioControllerLinearLayout.setVisibility(View.VISIBLE);
                        audioController = new AudioController(views.timeLine, views.seekBar, views.playStop, views.playPrev, views.playNext, getApplicationContext(), selectedUri);
                        audioController.initHandler(new Handler());

                        metadata.put("Duration", audioController.getDuration());
                    }

                    PostToAdd.metadata = metadata.toString();
                    // endregion
                } catch (IOException e) {
                    Log.d("ActivityResultLauncher & IOException: ", e.getMessage());
                } catch (JSONException e) {
                    Log.d("JSONException (setMetadata): ", e.getMessage());
                }
            }
        }
    });

    private void goneButtons() {
        views.btnVideo.setVisibility(View.GONE);
        views.btnImage.setVisibility(View.GONE);
        views.btnAudio.setVisibility(View.GONE);
    }

    @SuppressLint("SetTextI18n")
    private void setListeners() {
        views.btnVideo.setOnClickListener(v -> {
            OpenMedia.openGallery(this, MediaTypes.VIDEO, someActivityResultLauncher);
            goneButtons();
        });

        views.btnImage.setOnClickListener(v -> {
            OpenMedia.openGallery(this, MediaTypes.IMAGE, someActivityResultLauncher);
            goneButtons();
        });

        views.btnAudio.setOnClickListener(v -> {
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
                    views.postponePublication.setText(resources.getString(R.string.postpone_publication) + ": " + DateFormatting.formatDate(selectedDate));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);

        TimePickerDialog.OnTimeSetListener timeSetListener = (view, hourOfDay, minute) -> {
            selectedDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
            selectedDate.set(Calendar.MINUTE, minute);

            PostToAdd.postponePublication = selectedDate.getTime();
        };

        DatePickerDialog.OnDateSetListener dateSetListener = (view, year1, month1, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            if (year >= year1 && month >= month1 && dayOfMonth >= day) {
                views.postponePublication.setText(resources.getString(R.string.postpone_publication) + ": " + DateFormatting.formatDate(selectedDate));
            } else {
                Toast.makeText(this, R.string.birthday_error_date, Toast.LENGTH_SHORT).show();
            }
        };

        views.postponePublication.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreatePost.this, android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);
            TimePickerDialog timePickerDialog = new TimePickerDialog(CreatePost.this, android.R.style.Theme_DeviceDefault_Dialog, timeSetListener, hours, minutes, !Localisation.chosenLocale.getCountry().equals("EN"));

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            timePickerDialog.show();
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        views.close.setOnClickListener(v -> finish());

        views.done.setOnClickListener(v -> {
            if (mediaBytes == null || mediaBytes.length == 0) {
                Toast.makeText(getApplicationContext(), R.string.error_no_photo, Toast.LENGTH_SHORT).show();
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

            PostToAdd.description = views.description.getText().toString();
            String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
            try {
                JSONObject body = Post.getJSONToSetNewPost(login, PostToAdd.description, PostToAdd.metadata, PostToAdd.postponePublication, PostToAdd.taggedPeople);

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
                }, media, body.toString());
            } catch (JSONException e) {
                Log.d("JSONException: ", e.getMessage());
            }
            // endregion

            if (audioController != null) audioController.clearHandler();
            finish();
        });

        views.photoView.setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.IMAGE, someActivityResultLauncher));
        views.videoView.setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.VIDEO, someActivityResultLauncher));
        views.audioControllerLinearLayout.setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.AUDIO, someActivityResultLauncher));

        views.tagPeople.setOnClickListener(v -> setToTagPeople());
    }

    @SuppressLint("SetTextI18n")
    private void setStringResources() {
        views.description.setHint(resources.getString(R.string.add_description));
        views.title.setText(resources.getString(R.string.new_post));
        views.postponePublication.setText(resources.getString(R.string.postpone_publication));

        if (PostToAdd.taggedPeople != null) {
            views.tagPeople.setText(resources.getString(R.string.tag_people) + ": " + PostToAdd.taggedPeople);
        }

        if (selectedDate != null) {
            views.postponePublication.setText(resources.getString(R.string.postpone_publication) + ": " + DateFormatting.formatDate(selectedDate));
        } else {
            views.postponePublication.setText(resources.getString(R.string.postpone_publication));
        }
    }
}