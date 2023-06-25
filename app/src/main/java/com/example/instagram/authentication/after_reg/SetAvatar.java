package com.example.instagram.authentication.after_reg;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagram.R;
import com.example.instagram.authentication.Authorisation;
import com.example.instagram.main_process.NewsLine;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Errors;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.MediaTypes;
import com.example.instagram.services.OpenMedia;
import com.example.instagram.services.ReadBytesForMedia;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;

import org.json.JSONException;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SetAvatar extends AppCompatActivity {
    private Resources resources;
    private LinearLayout setPhoto;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private Button[] buttons;
    private byte[] imageBytes;
    private String extension;

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

        setListeners();
        Animation.getAnimations(setPhoto).start();
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void setIntents() {
        if (Intents.getSetFindContacts() == null)
            Intents.setSetFindContacts(new Intent(this, FindContactsFriends.class));

        if (Intents.getAuthorisation() == null)
            Intents.setAuthorisation(new Intent(this, Authorisation.class));

        Intents.setNewsList(new Intent(this, NewsLine.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(languages);
    }

    private void findViews() {
        setPhoto = findViewById(R.id.set_photo);
        languages = findViewById(R.id.languages);
        buttons = new Button[]{findViewById(R.id.photo_button)};
        textViews = new TextView[]{findViewById(R.id.add_photo_header), findViewById(R.id.add_photo_info), findViewById(R.id.other_variant), findViewById(R.id.skip), findViewById(R.id.reg_question), findViewById(R.id.link_log_in)};
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

        buttons[0].setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.IMAGE, someActivityResultLauncher));

        textViews[5].setOnClickListener(v -> {
            startActivity(Intents.getAuthorisation());
            finish();
        });

        textViews[3].setOnClickListener(v -> {
            startActivity(Intents.getNewsList());
            finish();
        });
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();

            if (data != null) {
                Uri selectedImageUri = data.getData();

                extension = FindExtension.getExtension(selectedImageUri, getApplicationContext());

                try {
                    imageBytes = ReadBytesForMedia.readBytes(this, selectedImageUri);

                    if (imageBytes == null) {
                        Toast.makeText(this, getString(R.string.tiramisu_or_better), Toast.LENGTH_SHORT).show();
                    }

                    sendAvaToBackEnd();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    });

    private void sendAvaToBackEnd() {
        if (imageBytes.length == 0) {
            Toast.makeText(getApplicationContext(), R.string.error_no_photo, Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody image = RequestBody.create(MediaType.parse(getString(R.string.mime_image) + "/" + extension), imageBytes);
        RequestBody nickName = RequestBody.create(MediaType.parse(getString(R.string.mime_text_plain)), TransitUser.user.getLogin());

        try {
            Services.sendAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String responseStr = response.body().toString();
                        Errors.sendAvatar(getApplicationContext(), responseStr).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                    Log.d("sendAvaToBackEnd: ", t.getMessage());
                }
            }, image, nickName);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        startActivity(Intents.getNewsList());
        finish();
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