package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.instagram.DAOs.Post;
import com.example.instagram.R;
import com.example.instagram.services.AudioController;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.GFG;
import com.example.instagram.services.GetFileInfo;
import com.example.instagram.services.Intents;
import com.example.instagram.services.MediaTypes;
import com.example.instagram.services.OpenMedia;
import com.example.instagram.services.ReadBytesForMedia;
import com.example.instagram.services.TagPeople;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class CreatePost extends AppCompatActivity {
    private class Views {
        private final LinearLayout audioControllerLinearLayout;
        private final LinearLayout createPostLayout;
        private final Button btnVideo;
        private final Button btnImage;
        private final Button btnAudio;
        private final ImageView close;
        private final ImageView done;
        private final ImageView playPrev;
        private final ImageView playNext;
        private final ImageView playStop;
        private final TextView timeLine;
        private final TextView tagPeople;
        private final TextView postponePublication;
        private final EditText description;
        private final SeekBar seekBar;
        private final VideoView videoView;
        private final ImageView photoView;
        private final CardView imageCard;
        private final CardView videoCard;
        private final CardView audioCard;

        public Views() {
            audioControllerLinearLayout = findViewById(R.id.audio_controller);
            createPostLayout = findViewById(R.id.create_post);
            timeLine = findViewById(R.id.time_line);
            playPrev = findViewById(R.id.play_prev);
            playNext = findViewById(R.id.play_next);
            playStop = findViewById(R.id.play_stop);
            btnVideo = findViewById(R.id.btn_video);
            btnAudio = findViewById(R.id.btn_audio);
            btnImage = findViewById(R.id.btn_image);
            close = findViewById(R.id.close_page);
            done = findViewById(R.id.done);
            tagPeople = findViewById(R.id.tag_people);
            postponePublication = findViewById(R.id.postpone_publication);
            description = findViewById(R.id.new_post_description);
            seekBar = findViewById(R.id.seek_bar);
            videoView = findViewById(R.id.video_content);
            photoView = findViewById(R.id.preview_photo);
            imageCard = findViewById(R.id.photo_card);
            videoCard = findViewById(R.id.video_card);
            audioCard = findViewById(R.id.audio_card);
        }
    }

    public static class PostToAdd {
        public static String metadata;
        public static Date postponePublication;
        public static String description;
        public static String taggedPeople;
    }

    private AudioController audioController;
    private String extension;
    private TagPeople tagPeople;
    private Calendar selectedDate;
    private Views views;
    private byte[] mediaBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        views = new Views();
        tagPeople = new TagPeople(this, getResources(), this);

        setIntents();
        setListeners();
        UiVisibility.setUiVisibility(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setStringResources();
        ThemesBackgrounds.loadBackground(this, views.createPostLayout);
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

                long fileSizeInBytes = GetFileInfo.getSize(CreatePost.this, selectedUri);
                long fileSizeInKB = fileSizeInBytes / 1024;
                long fileSizeInMB = fileSizeInKB / 1024;

                if (fileSizeInMB > 1) {
                    Toast.makeText(CreatePost.this, getString(R.string.size_restriction), Toast.LENGTH_SHORT).show();
                    return;
                }

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
                        views.imageCard.setVisibility(View.VISIBLE);
                        selectedImage = BitmapFactory.decodeByteArray(mediaBytes, 0, mediaBytes.length);

                        if (selectedImage != null) {
                            views.photoView.setImageURI(selectedUri);
                            metadata.put("ViewSize", selectedImage.getHeight() + "x" + selectedImage.getWidth());
                        }
                    } else if (mime.contains(getString(R.string.mime_video))) {
                        // set video
                        views.videoCard.setVisibility(View.VISIBLE);
                        views.videoView.setVideoURI(selectedUri);
                        views.videoView.requestFocus();
                        views.videoView.start();
                        metadata.put("ViewSize", views.videoView.getHeight() + "x" + views.videoView.getWidth());
                        metadata.put("Duration", GFG.convert(views.videoView.getDuration()));
                    } else if (mime.contains(getString(R.string.mime_audio))) {
                        // set audio
                        views.audioControllerLinearLayout.setVisibility(View.VISIBLE);
                        views.audioCard.setVisibility(View.VISIBLE);
                        audioController = new AudioController(views.timeLine, views.seekBar, views.playStop, views.playPrev, views.playNext, getApplicationContext(), selectedUri);
                        audioController.initHandler(new Handler());
                        metadata.put("Duration", GFG.convert(audioController.getDuration()));
                    }

                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("bytes", fileSizeInBytes);
                    jsonObject.put("kilobytes", fileSizeInKB);
                    jsonObject.put("megabytes", fileSizeInMB);

                    metadata.put("FileName", GetFileInfo.getName(CreatePost.this, selectedUri));
                    metadata.put("FileSize", jsonObject);
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
                views.postponePublication.setText(getResources().getString(R.string.postpone_publication) + ": " + DateFormatting.formatDateWithTime(selectedDate));
            } else {
                Toast.makeText(this, R.string.birthday_error_date, Toast.LENGTH_SHORT).show();
            }
        };

        views.postponePublication.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(CreatePost.this, android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);
            TimePickerDialog timePickerDialog = new TimePickerDialog(CreatePost.this, android.R.style.Theme_DeviceDefault_Dialog, timeSetListener, hours, minutes, !Locale.getDefault().getCountry().equals("EN") || !Locale.getDefault().getCountry().equals("US"));

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
                body.put("token", Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString()));
                new DoCallBack().setValues(null, this, new Object[]{media, body.toString()}).sendMultipartPost();
            } catch (JSONException e) {
                throw new RuntimeException(e);
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
        if (PostToAdd.taggedPeople != null) {
            views.tagPeople.setText(getResources().getString(R.string.tag_people) + ": " + PostToAdd.taggedPeople);
        }

        if (selectedDate != null) {
            views.postponePublication.setText(getResources().getString(R.string.postpone_publication) + ": " + DateFormatting.formatDate(selectedDate));
        } else {
            views.postponePublication.setText(getResources().getString(R.string.postpone_publication));
        }
    }
}