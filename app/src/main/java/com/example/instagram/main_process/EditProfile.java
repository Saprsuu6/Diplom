package com.example.instagram.main_process;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.example.instagram.authentication.after_reg.SetBirthday;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Errors;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.MediaTypes;
import com.example.instagram.services.OpenMedia;
import com.example.instagram.services.ReadBytesForMedia;
import com.example.instagram.services.Services;
import com.example.instagram.services.Animation;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
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
    private Button[] buttons;
    private ImageView[] imageViews;
    private Calendar selectedDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private byte[] imageBytes;
    private String extension;
    private Handler handler;
    private Runnable runnable;

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

        setUserInfo();

        handler = new Handler();
        runnable = checkUsedLink();
        handler.postDelayed(runnable, 5000L);
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
        editTexts = new EditText[]{findViewById(R.id.info_for_name), findViewById(R.id.info_for_surname), findViewById(R.id.info_for_email), findViewById(R.id.info_for_bio), findViewById(R.id.birth_date), findViewById(R.id.info_for_email_code)};
        imageViews = new ImageView[]{findViewById(R.id.ava), findViewById(R.id.close), findViewById(R.id.done), findViewById(R.id.ava), findViewById(R.id.validation_error_email), findViewById(R.id.validation_error_birthday)};
        textViews = new TextView[]{findViewById(R.id.title), findViewById(R.id.email_name)};
        buttons = new Button[]{findViewById(R.id.send_new_email), findViewById(R.id.confirm_code_button)};
    }

    private void setUserInfo() {
        editTexts[0].setText(SelfPage.userPage.getNickName());
        editTexts[1].setText(SelfPage.userPage.getSurname());

        editTexts[2].setText(SelfPage.userPage.getEmail());
        editTexts[3].setText(SelfPage.userPage.getDescription());
        editTexts[4].setText(DateFormatting.formatDate(SelfPage.userPage.getBirthday()));
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

        editTexts[2].addTextChangedListener(new Validator(editTexts[2]) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editTexts[2].setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validateEmail(text, "", resources);
                    setValidationError(imageViews[4], false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()));
                } catch (Exception exception) {
                    setValidationError(imageViews[4], true, getResources().getString(R.string.error_send_password1));
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
                setValidationError(imageViews[5], true, getResources().getString(R.string.birthday_error_age));
            } else {
                editTexts[4].setText(DateFormatting.formatDate(selectedDate));
                setValidationError(imageViews[5], false, "");
            }
        };
        // endregion

        imageViews[0].setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.IMAGE, someActivityResultLauncher));

        imageViews[1].setOnClickListener(v -> {
            try {
                Services.sendToGetCurrentUser(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            JSONObject user;

                            try {
                                user = new JSONObject(response.body());

                                SelfPage.userPage = User.getPublicUser(user, TransitUser.user.getLogin());
                                finish();
                            } catch (JSONException | ParseException e) {
                                Log.d("JSONException", e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        Log.d("sendToGetCurrentUser: (onFailure)", t.getMessage());
                    }
                }, TransitUser.user.getLogin());
            } catch (JSONException e) {
                Log.d("JSONException: ", e.getMessage());
            }
        });

        imageViews[2].setOnClickListener(v -> {
            if (imageViews[4].getVisibility() == View.GONE && imageViews[5].getVisibility() == View.GONE) {
                SelfPage.userPage.setLogin(editTexts[0].getText().toString().trim());
                SelfPage.userPage.setSurname(editTexts[1].getText().toString().trim());
                SelfPage.userPage.setDescription(editTexts[3].getText().toString().trim());

                if (selectedDate == null) {
                    selectedDate = DateFormatting.getCalendar(SelfPage.userPage.getBirthday());
                }

                SelfPage.userPage.setBirthday(selectedDate.getTime());

                sendAvaToBackEnd();

                JSONObject toChange;
                try {
                    toChange = SelfPage.userPage.getToChange();

                    Services.sendToChangeUser(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                if (response.body().contains("3")) {
                                    Toast.makeText(getApplicationContext(), getString(R.string.email_are_not_valid), Toast.LENGTH_SHORT).show();
                                }
                            }
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
        });

        // send code to mail
        buttons[0].setOnClickListener(v -> {
            if (SelfPage.userPage.getEmail().equals(editTexts[2].getText().toString())) {
                JSONObject body = new JSONObject();
                try {
                    body.put("login", SelfPage.userPage.getLogin());
                    body.put("newEmail", editTexts[2].getText().toString());
                    body.put("token", TransitUser.user.getToken());

                    Services.sendToSendCodeForEmail(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Errors.forgotPasswordCode(getApplicationContext(), response.body()).show();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                            Log.d("sendToSendCodeForEmail: (onFailure) ", t.getMessage());
                        }
                    }, body.toString());
                } catch (JSONException e) {
                    Log.d("JSONException: ", e.getMessage());
                }
            }
        });

        // check code
        buttons[1].setOnClickListener(v -> {
            if (editTexts[5].getText().length() > 0) {
                if (!SelfPage.userPage.getEmail().equals(editTexts[2].getText().toString())) {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("token", TransitUser.user.getToken());
                        body.put("code", editTexts[5].getText());
                        body.put("newEmail", editTexts[2].getText().toString());

                        Services.sendToChangeEmailFinally(new Callback<>() {
                            @Override
                            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    Errors.emailCodes(getApplicationContext(), response.body()).show();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                                Log.d("sendToSendCodeForEmail: (onFailure) ", t.getMessage());
                            }
                        }, body.toString());
                    } catch (JSONException e) {
                        Log.d("JSONException: ", e.getMessage());
                    }
                }
            }
        });
    }

    // check link
    private Runnable checkUsedLink() {
        return () -> {
            try {
                Services.sendToCheckUsedLickInMail(new Callback<>() {
                    @Override
                    public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().contains("0")) {
                                String responseStr = response.body();
                                int index = responseStr.indexOf(":");
                                responseStr = responseStr.substring(index + 1);
                                editTexts[5].setText(responseStr.trim());
                            } else {
                                handler.postDelayed(runnable, 5000L);
                            }
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                        System.out.println(t.getMessage());
                    }
                });

            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        };
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

            if (data != null) {
                Uri selectedImageUri = data.getData();

                extension = FindExtension.getExtension(selectedImageUri, getApplicationContext());

                try {
                    imageBytes = ReadBytesForMedia.readBytes(this, selectedImageUri);

                    if (imageBytes == null) {
                        Toast.makeText(this, getString(R.string.tiramisu_or_better), Toast.LENGTH_SHORT).show();
                    }

                    //Bitmap ava = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                    imageViews[0].setImageURI(selectedImageUri);
                    //Glide.with(getApplicationContext()).load(ava).diskCacheStrategy(DiskCacheStrategy.ALL).into(imageViews[0]);
                } catch (IOException e) {
                    Log.d("IOException: ", e.getMessage());
                }
            }
        }
    });

    private void sendAvaToBackEnd() {
        if (imageBytes != null) {
            if (imageBytes.length == 0) {
                Toast.makeText(getApplicationContext(), R.string.error_no_photo, Toast.LENGTH_SHORT).show();
                return;
            }
        } else {
            return;
        }

        RequestBody image = RequestBody.create(MediaType.parse(getString(R.string.mime_image) + "/" + extension), imageBytes);
        RequestBody login = RequestBody.create(MediaType.parse(getString(R.string.mime_text_plain)), TransitUser.user.getLogin());

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
        editTexts[2].setHint(resources.getString(R.string.login_hint_email));
        editTexts[5].setHint(resources.getString(R.string.mail_code));

        buttons[0].setText(resources.getString(R.string.send_code));
        buttons[1].setText(resources.getString(R.string.confirm_code));
    }

    // endregion

}