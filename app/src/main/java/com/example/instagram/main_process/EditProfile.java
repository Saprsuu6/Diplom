package com.example.instagram.main_process;

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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.authentication.after_reg.SetBirthday;
import com.example.instagram.services.Animation;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.Errors;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.Intents;
import com.example.instagram.services.Localisation;
import com.example.instagram.services.MediaTypes;
import com.example.instagram.services.OpenMedia;
import com.example.instagram.services.ReadBytesForMedia;
import com.example.instagram.services.Services;
import com.example.instagram.services.TransitUser;
import com.example.instagram.services.UiVisibility;
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
    private class Views {
        private final LinearLayout editProfileLayout;
        private final Spinner languagesSpinner;
        private final ImageView close;
        private final ImageView done;
        private final ImageView avatar;
        private final TextView title;
        private final EditText userName;
        private final EditText surName;
        private final EditText birthday;
        private final EditText bio;
        private final EditText email;
        private final EditText confirmCode;
        private final Button sendCode;
        private final Button confirmCodeButton;
        private final ImageView warningEmail;
        private final ImageView warningBirthday;

        public Views() {
            editProfileLayout = findViewById(R.id.edit_profile);
            languagesSpinner = findViewById(R.id.languages);
            close = findViewById(R.id.close);
            done = findViewById(R.id.done);
            title = findViewById(R.id.title);
            avatar = findViewById(R.id.ava);
            userName = findViewById(R.id.info_for_name);
            surName = findViewById(R.id.info_for_surname);
            birthday = findViewById(R.id.birth_date);
            bio = findViewById(R.id.info_for_bio);
            email = findViewById(R.id.info_for_email);
            confirmCode = findViewById(R.id.info_for_email_code);
            sendCode = findViewById(R.id.send_new_email);
            confirmCodeButton = findViewById(R.id.confirm_code_button);
            warningEmail = findViewById(R.id.validation_error_email);
            warningBirthday = findViewById(R.id.validation_error_birthday);
        }
    }

    private Resources resources;
    private Localisation localisation;
    private Calendar selectedDate;
    private DatePickerDialog.OnDateSetListener dateSetListener;
    private String extension;
    private Handler handler;
    private Runnable runnable;
    private Views views;
    private byte[] imageBytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        resources = getResources();
        handler = new Handler();
        runnable = checkUsedLink();
        handler.postDelayed(runnable, 5000L);
        views = new Views();
        localisation = new Localisation(this);
        views.languagesSpinner.setAdapter(localisation.getAdapter());
        views.birthday.setInputType(InputType.TYPE_NULL);

        LoadAvatar();
        setListeners();
        UiVisibility.setUiVisibility(this);

        setUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Localisation.setFirstLocale(views.languagesSpinner);
    }

    private void LoadAvatar() {
        // region send request to get avatar
        try {
            Services.sendToGetAva(new Callback<>() {
                @Override
                public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String avaLink = response.body();
                        String imagePath = Services.BASE_URL + getString(R.string.root_folder) + avaLink;
                        Glide.with(getApplicationContext()).load(imagePath).diskCacheStrategy(DiskCacheStrategy.ALL).into(views.avatar);
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

    private void setUserInfo() {
        views.userName.setText(SelfPage.userPage.getNickName());
        views.surName.setText(SelfPage.userPage.getSurname());
        views.email.setText(SelfPage.userPage.getEmail());
        views.bio.setText(SelfPage.userPage.getDescription());
        views.birthday.setText(DateFormatting.formatDate(SelfPage.userPage.getBirthday()));
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
        views.languagesSpinner.setOnItemSelectedListener(itemLocaliseSelectedListener);

        views.email.addTextChangedListener(new Validator(views.email) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                editText.setTextColor(resources.getColor(R.color.white, getTheme()));

                try {
                    Validations.validateEmail(text, "", resources);
                    setValidationError(views.warningEmail, false, "");
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()));
                } catch (Exception exception) {
                    setValidationError(views.warningEmail, true, getResources().getString(R.string.error_send_password1));
                    editText.setBackground(resources.getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()));
                }
            }
        });

        // region set birthday
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        views.birthday.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_DeviceDefault_Dialog, dateSetListener, year, month, day);

            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
            datePickerDialog.show();
        });

        dateSetListener = (view, year1, month1, dayOfMonth) -> {
            selectedDate = Calendar.getInstance();
            selectedDate.set(year1, month1, dayOfMonth);

            DateFormatting.setSimpleDateFormat(Locale.getDefault().getCountry());

            if (year - SetBirthday.yearOffset <= year1) {
                setValidationError(views.warningBirthday, true, getResources().getString(R.string.birthday_error_age));
            } else {
                views.birthday.setText(DateFormatting.formatDate(selectedDate));
                setValidationError(views.warningBirthday, false, "");
            }
        };
        // endregion

        views.avatar.setOnClickListener(v -> OpenMedia.openGallery(this, MediaTypes.IMAGE, someActivityResultLauncher));

        views.close.setOnClickListener(v -> {
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

        views.done.setOnClickListener(v -> {
            if (views.warningBirthday.getVisibility() == View.GONE && views.warningEmail.getVisibility() == View.GONE) {
                if (selectedDate == null) {
                    selectedDate = DateFormatting.getCalendar(SelfPage.userPage.getBirthday());
                }

                SelfPage.userPage.setBirthday(selectedDate.getTime());
                SelfPage.userPage.setDescription(views.bio.getText().toString().trim());
                SelfPage.userPage.setNickName(views.userName.getText().toString().trim());
                SelfPage.userPage.setSurname(views.surName.getText().toString().trim());
                SelfPage.userPage.setDescription(views.bio.getText().toString().trim());

                sendAvaToBackEnd();

                JSONObject toChange;
                try {
                    toChange = SelfPage.userPage.getToChange();

                    Services.sendToChangeUser(new Callback<>() {
                        @Override
                        public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                Errors.editProfile(getApplicationContext(), response.body()).show();
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
        views.sendCode.setOnClickListener(v -> {
            if (!SelfPage.userPage.getEmail().equals(views.email.getText().toString())) {
                JSONObject body = new JSONObject();
                try {
                    body.put("login", SelfPage.userPage.getLogin());
                    body.put("newEmail", views.email.getText().toString());
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
        views.confirmCodeButton.setOnClickListener(v -> {
            if (views.confirmCode.getText().length() > 0) {
                if (!SelfPage.userPage.getEmail().equals(views.confirmCodeButton.getText().toString())) {
                    JSONObject body = new JSONObject();
                    try {
                        body.put("token", TransitUser.user.getToken());
                        body.put("code", views.confirmCode.getText());
                        body.put("newEmail", views.email.getText().toString());

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
                                views.confirmCode.setText(responseStr.trim());
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

                    views.avatar.setImageURI(selectedImageUri);
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

    private void setStringResources() {
        views.title.setText(resources.getString(R.string.change_your_info_to_be_individual));
        views.userName.setHint(resources.getString(R.string.login_hint_username));
        views.surName.setHint(resources.getString(R.string.login_hint_surname));
        views.email.setHint(resources.getString(R.string.login_hint_email));
        views.birthday.setHint(resources.getString(R.string.login_hint_bio));
        views.birthday.setHint(resources.getString(R.string.birthday_hint));
        views.confirmCode.setHint(R.string.confirm_code);
        views.sendCode.setText(resources.getString(R.string.send_code));
        views.confirmCodeButton.setText(resources.getString(R.string.confirm_code));
    }
}