package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Multipart;
import com.example.instagram.services.MyRetrofit;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.TransitUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

public class SetAvatar extends AppCompatActivity {
    private Resources resources;
    private LinearLayout setPhoto;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private Button[] buttons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_avatar);
        setUiVisibility();

        resources = getResources();
        setIntents();
        findViews();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());
        Localisation.setFirstLocale(languages);

        setListeners();
        setAnimations();
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setIntents() {
        if (Intents.getSetFindContacts() == null)
            Intents.setSetFindContacts(new Intent(this, FindContactsFriends.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));
    }

    @Override
    protected void onResume() {
        Localisation.setFirstLocale(languages);
        super.onResume();
    }

    private void setAnimations() {
        AnimationDrawable animationDrawable = (AnimationDrawable) setPhoto.getBackground();
        animationDrawable.setExitFadeDuration(4000);
        animationDrawable.start();
    }

    private void findViews() {
        setPhoto = findViewById(R.id.set_photo);
        languages = findViewById(R.id.languages);
        buttons = new Button[]{findViewById(R.id.photo_button)};
        textViews = new TextView[]{findViewById(R.id.add_photo_header),
                findViewById(R.id.add_photo_info),
                findViewById(R.id.other_variant),
                findViewById(R.id.skip),
                findViewById(R.id.reg_question),
                findViewById(R.id.link_log_in)};
    }

    @SuppressLint({"ClickableViewAccessibility", "IntentReset"})
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

        buttons[0].setOnClickListener(v -> {
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
        });

        textViews[5].setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();
                    assert data != null;
                    Uri selectedImageUri = data.getData();

                    String fileContent = getFileContent(selectedImageUri);
                    //String fileName = getFileName(selectedImageUri);
                    //File ava = new File(fileContent);

                    // initialise retrofit
                    String link = "https://picsum.photos"; // TODO change way
                    //RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), ava);

                    Retrofit retrofit = MyRetrofit.initializeRetrofit(link);

                    Multipart mainInterface = retrofit.create(Multipart.class);

                    // initialize call
                    Call<String> call = mainInterface.UPLOAD("image/jpeg", fileContent);
                    call.enqueue(new Callback<String>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, retrofit2.Response<String> response) {
                            System.out.println("Upload success");
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, Throwable t) {
                            System.out.println("Upload error: " + t.getMessage());
                        }
                    });
//                    File ava = new File(String.valueOf(selectedImageUri));
//                    if (selectedImageUri != null) {
//                        System.out.println(ava);
//                        // TODO find how to know extension
//                    }
                }
            }
    );

    public String getFileContent(Uri contentUri) {
        try {
            InputStream in = getContentResolver().openInputStream(contentUri);
            if (in != null) {
                BufferedReader r = new BufferedReader(new InputStreamReader(in));
                StringBuilder total = new StringBuilder();
                for (String line; (line = r.readLine()) != null; ) {
                    total.append(line).append('\n'); // TODO \n
                }
                return total.toString();
            } else {
                System.out.println("TAG: Input stream is null");
            }
        } catch (Exception e) {
            System.out.println("TAG. Error while reading file by uri: " + e);
        }
        return "Could not read content!";
    }

    @SuppressLint("Range")
    public String getFileName(Uri contentUri) {
        String result = null;
        if (contentUri.getScheme() != null && contentUri.getScheme().equals("content")) {
            try (Cursor cursor = getContentResolver().query(contentUri, null, null, null, null)) {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            }
        }
        if (result == null) {
            result = contentUri.getPath();
            if (result == null) {
                return null;
            }
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // region Localisation
    private void setStringResources() {
        buttons[0].setText(resources.getString(R.string.add_photo_btn_info));

        textViews[0].setText(resources.getString(R.string.add_photo_header));
        textViews[1].setText(resources.getString(R.string.add_photo_info));
        textViews[2].setText(resources.getString(R.string.or));
        textViews[3].setText(resources.getString(R.string.add_btn_skip));
        textViews[4].setText(resources.getString(R.string.have_an_acc));
        textViews[5].setText(resources.getString(R.string.log_in));
    }
    // endregion
}