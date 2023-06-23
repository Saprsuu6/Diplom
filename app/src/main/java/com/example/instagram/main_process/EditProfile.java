package com.example.instagram.main_process;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

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
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.authentication.Registration;
import com.example.instagram.authentication.after_reg.SetBirthday;
import com.example.instagram.authentication.after_reg.SetPassword;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Errors;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.Permissions;
import com.example.instagram.services.Services;
import com.example.instagram.services.Animation;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import org.json.JSONException;
import org.json.JSONObject;

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

public class EditProfile extends AppCompatActivity {
    private Resources resources;
    private LinearLayout editProfile;
    // region localisation
    private Localisation localisation;
    private Spinner languages;
    // endregion
    private TextView[] textViews;
    private EditText[] editTexts;
    private ImageView[] imageViews;
    private Calendar selectedDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private byte[] imageBytes;
    private String extension;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        setUiVisibility();

        resources = getResources();
        findViews();
        LoadAvatar();

        localisation = new Localisation(this);
        languages.setAdapter(localisation.getAdapter());

        editTexts[4].setInputType(InputType.TYPE_NULL);

        setListeners();
        Animation.getAnimations(editProfile);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(languages);
    }

    private void LoadAvatar() {
        // region send request to get avatar
        try {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avaLink = response.body();

                        // set ava
                        Glide.with(getApplicationContext()).load(Services.BASE_URL + avaLink).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViews[0]);
                    }
                }

                @Override
                public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                    Log.d("sendToGetAva: (onFailure)", t.getMessage());
                }
            }, SelfPage.userPage.getLogin());
        } catch (JSONException e) {
            Log.d("sendToGetAva: (JSONException)", e.getMessage());
        }
        // endregion
    }

    private void setUiVisibility() {
        Window w = getWindow();
        w.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void findViews() {
        editProfile = findViewById(R.id.edit_profile);
        languages = findViewById(R.id.languages);
        editTexts = new EditText[]{findViewById(R.id.info_for_name), findViewById(R.id.info_for_surname), findViewById(R.id.info_for_email), findViewById(R.id.info_for_bio), findViewById(R.id.birth_date)};
        imageViews = new ImageView[]{findViewById(R.id.ava), findViewById(R.id.close), findViewById(R.id.done), findViewById(R.id.ava), findViewById(R.id.validation_error_name), findViewById(R.id.validation_error_surname), findViewById(R.id.validation_error_email), findViewById(R.id.validation_error_birthday)};
        textViews = new TextView[]{findViewById(R.id.title), findViewById(R.id.email_name)};
    }

    @SuppressLint("IntentReset")
    private void setListeners() {
        AdapterView.OnItemSelectedListener itemLocaliseSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Configuration configuration = Localisation.setLocalize(parent, localisation, position);
                getBaseContext().getResources().updateConfiguration(configuration, null);
                setStringResources();

                DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };
        languages.setOnItemSelectedListener(itemLocaliseSelectedListener);

        imageViews[0].setOnClickListener(v -> finish());
        // name
        editTexts[0].addTextChangedListener(new Validator(editTexts[0]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[0].setTextColor(resources.getColor(R.color.white, getTheme()));

                if (editTexts[0].length() != 0) {
                    setValidationError(imageViews[4], false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()));
                } else {
                    setValidationError(imageViews[4], true, getResources().getString(R.string.error_send_password1));
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()));
                }
            }
        });
        // surname
        editTexts[1].addTextChangedListener(new Validator(editTexts[1]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[1].setTextColor(resources.getColor(R.color.white, getTheme()));

                if (editTexts[1].length() != 0) {
                    setValidationError(imageViews[5], false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()));
                } else {
                    setValidationError(imageViews[5], true, getResources().getString(R.string.error_send_password1));
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()));
                }
            }
        });
        // surname
        editTexts[2].addTextChangedListener(new Validator(editTexts[2]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[2].setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validateEmail(text, textViews[1].getText().toString(), resources);
                    setValidationError(imageViews[6], false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()));
                } catch (Exception exception) {
                    setValidationError(imageViews[6], true, getResources().getString(R.string.error_send_password1));
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()));
                }
            }
        });

        // region set birthday
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        editTexts[4].setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        dateSetListener = (view, year1, month1, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());

            if (year - SetBirthday.yearOffset <= year1) {
                setValidationError(imageViews[7], true, getResources().getString(R.string.birthday_error_age));
            } else {
                editTexts[4].setText(DateFormatting.formatDate(selectedDate));
                setValidationError(imageViews[7], false, "");
            }
        };
        // endregion

        imageViews[0].setOnClickListener(v -> {
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
        });

        imageViews[1].setOnClickListener(v -> finish());

        imageViews[2].setOnClickListener(v -> {
            if (imageViews[4].getVisibility() == View.GONE && imageViews[5].getVisibility() == View.GONE && imageViews[6].getVisibility() == View.GONE && imageViews[7].getVisibility() == View.GONE) {
                SelfPage.userPage.setLogin(editTexts[0].getText().toString().trim());
                SelfPage.userPage.setSurname(editTexts[1].getText().toString().trim());
                SelfPage.userPage.setEmail(editTexts[2].getText().toString().trim() + textViews[1].getText());
                SelfPage.userPage.setDescription(editTexts[3].getText().toString().trim());
                SelfPage.userPage.setBirthday(selectedDate.getTime());

                sendAvaToBackEnd();

                JSONObject toChange;
                try {
                    toChange = SelfPage.userPage.getToChange();

                    Services.sendToChangeUser(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            Log.d("sendToChangeUser: (onResponse) ", response.body()); // TODO
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToChangeUser: (onFailure) ", t.getMessage());
                        }
                    }, toChange.toString());
                } catch (JSONException e) {
                    Log.d("JSONException", e.getMessage());
                }
            }

            finish();
        });
    }

    private void setValidationError(View view, boolean temp, String message) {
        if (temp) {
            view.setVisibility(View.VISIBLE);
            Animation.getAnimations(view).start();
        } else {
            view.setVisibility(View.GONE);
            Animation.getAnimations(view).stop();
        }

        TooltipCompat.setTooltipText(view, message);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Intent data = result.getData();
            assert data != null;
            Uri selectedImageUri = data.getData();

            extension = FindExtension.getExtension(selectedImageUri, getApplicationContext());

            try {
                InputStream imageStream = getContentResolver().openInputStream(selectedImageUri);

                // region Get image bytes
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    imageBytes = imageStream.readAllBytes();
                }
                //endregion

                Bitmap ava = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                Glide.with(getApplicationContext()).load(ava).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViews[0]);
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    });

    private void sendAvaToBackEnd() {
        if (imageBytes.length == 0) {
            Toast.makeText(getApplicationContext(), R.string.error_no_photo, Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody image = RequestBody.create(MediaType.parse("image/" + extension), imageBytes);
        RequestBody login = RequestBody.create(MediaType.parse("text/plain"), TransitUser.user.getLogin());

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
            }, image, login);
        } catch (JSONException e) {
            System.out.println(e.getMessage());
        }

        startActivity(Intents.getNewsList());
        finish();
    }

    // region Localisation
    private void setStringResources() {
        textViews[0].setText(resources.getString(R.string.change_your_info_to_be_individual));
        editTexts[0].setHint(resources.getString(R.string.login_hint_username));
        editTexts[1].setHint(resources.getString(R.string.login_hint_surname));
        editTexts[2].setHint(resources.getString(R.string.login_hint_email));
        editTexts[3].setHint(resources.getString(R.string.login_hint_bio));
        editTexts[4].setHint(resources.getString(R.string.birthday_hint));
    }

    // endregion

}