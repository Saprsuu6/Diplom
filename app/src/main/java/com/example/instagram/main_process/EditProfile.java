package com.example.instagram.main_process;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.TooltipCompat;

import com.example.instagram.DAOs.User;
import com.example.instagram.R;
import com.example.instagram.authentication.after_reg.SetBirthday;
import com.example.instagram.services.Animation;
import com.example.instagram.services.Cache;
import com.example.instagram.services.CacheScopes;
import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.DoCallBack;
import com.example.instagram.services.FindExtension;
import com.example.instagram.services.GetFileInfo;
import com.example.instagram.services.MediaTypes;
import com.example.instagram.services.OpenMedia;
import com.example.instagram.services.ReadBytesForMedia;
import com.example.instagram.services.Resources;
import com.example.instagram.services.UiVisibility;
import com.example.instagram.services.Validations;
import com.example.instagram.services.Validator;
import com.example.instagram.services.themes_and_backgrounds.ThemesBackgrounds;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class EditProfile extends AppCompatActivity {
    private class Views {
        private final LinearLayout editProfileLayout;
        private final ImageView close;
        private final ImageView done;
        private final ImageView avatar;
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
            close = findViewById(R.id.close_page);
            done = findViewById(R.id.done);
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

        handler = new Handler();
        runnable = checkUsedLink();
        handler.postDelayed(runnable, 5000L);
        views = new Views();
        views.birthday.setInputType(InputType.TYPE_NULL);

        try {
            LoadAvatar();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        setListeners();
        UiVisibility.setUiVisibility(this);

        setUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ThemesBackgrounds.loadBackground(this, views.editProfileLayout);
    }

    private void LoadAvatar() throws JSONException {
        new DoCallBack().setValues(null, this, new Object[]{UserPage.userPage.getLogin(), views.avatar}).sendToGetAvaImage();
    }

    private void setUserInfo() {
        views.userName.setText(UserPage.userPage.getNickName());
        views.surName.setText(UserPage.userPage.getSurname());
        views.email.setText(UserPage.userPage.getEmail());
        views.bio.setText(!UserPage.userPage.getDescription().equals("") ? UserPage.userPage.getDescription() : "");
        views.birthday.setText(DateFormatting.formatDate(UserPage.userPage.getBirthday()));
    }

    @SuppressLint("IntentReset")
    private void setListeners() {
        views.email.addTextChangedListener(new Validator(views.email) {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void validate(EditText editText, String text) {
                Resources.setTextColor(getResources().getColor(R.color.white, getTheme()), editText);

                try {
                    Validations.validateEmail(text, "", getResources());
                    setValidationError(views.warningEmail, false, "");
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_success, getTheme()), editText);
                } catch (Exception exception) {
                    setValidationError(views.warningEmail, true, getResources().getString(R.string.error_send_password1));
                    Resources.setBackgroundForEditText(getResources().getDrawable(R.drawable.edit_text_auto_reg_error, getTheme()), editText);
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
            String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());

            try {
                new DoCallBack().setValues(this::finish, this, new Object[]{login}).sendToGetCurrentUser();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        views.done.setOnClickListener(v -> {
            if (views.warningBirthday.getVisibility() == View.GONE && views.warningEmail.getVisibility() == View.GONE) {
                if (selectedDate == null) {
                    selectedDate = DateFormatting.getCalendar(UserPage.userPage.getBirthday());
                }

                UserPage.userPage.setBirthday(selectedDate.getTime());
                UserPage.userPage.setDescription(views.bio.getText().toString().trim());
                UserPage.userPage.setNickName(views.userName.getText().toString().trim());
                UserPage.userPage.setSurname(views.surName.getText().toString().trim());
                UserPage.userPage.setDescription(views.bio.getText().toString().trim());

                try {
                    sendAvaToBackEnd();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }

                String token = Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString());

                try {
                    JSONObject jsonObject = UserPage.userPage.getToChange(token);
                    new DoCallBack().setValues(null, this, new Object[]{jsonObject}).sendToChangeUser();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // send code to mail
        views.sendCode.setOnClickListener(v -> {
            if (!UserPage.userPage.isEmailConfirmed()) {
                String newEmail = views.email.getText().toString().trim();
                String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
                String token = Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString());

                try {
                    JSONObject jsonObject = User.getJSONToSendCodeForEmail(login, newEmail, token);
                    new DoCallBack().setValues(null, this, new Object[]{jsonObject}).sendToSendCodeForChangeEmail();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        // check code
        views.confirmCodeButton.setOnClickListener(v -> {
            if (views.confirmCode.getText().length() > 0 && !UserPage.userPage.isEmailConfirmed()) {
                String code = views.confirmCode.getText().toString().trim();
                String newEmail = views.email.getText().toString().trim();
                String token = Cache.loadStringSP(this, CacheScopes.USER_TOKEN.toString());

                try {
                    JSONObject jsonObject = User.getJSONToChangeEmailFinally(code, newEmail, token);
                    new DoCallBack().setValues(null, this, new Object[]{jsonObject}).sendToChangeEmailFinally();
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // check link
    private Runnable checkUsedLink() {
        return () -> {
            String login = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());

            try {
                handler.removeCallbacks(runnable);
                // check used link when change email
                new DoCallBack().setValues(null, this, new Object[]{login, views.confirmCode, handler, runnable}).sendToCheckUsedLickInMailEmail();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            handler.postDelayed(runnable, 5000L);
        };
    }

    private void setValidationError(View view, boolean temp, String message) {
        if (temp) {
            Resources.setVisibility(View.VISIBLE, view);
            Animation.getAnimations(view).start();
        } else {
            Resources.setVisibility(View.GONE, view);
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
                long fileSizeInBytes = GetFileInfo.getSize(this, selectedImageUri);

                try {
                    imageBytes = ReadBytesForMedia.readBytes(this, selectedImageUri, fileSizeInBytes);
                    views.avatar.setImageURI(selectedImageUri);
                } catch (IOException e) {
                    Log.d("IOException: ", e.getMessage());
                }
            }
        }
    });

    private void sendAvaToBackEnd() throws JSONException {
        if (imageBytes != null) {
            if (imageBytes.length == 0) {
                Resources.getToast(this, getString(R.string.error_no_photo)).show();
                return;
            }
        } else {
            return;
        }

        String loginFromContext = Cache.loadStringSP(this, CacheScopes.USER_LOGIN.toString());
        JSONObject jsonObject = new JSONObject().put("login", loginFromContext);
        RequestBody image = RequestBody.create(MediaType.parse(getString(R.string.mime_image) + "/" + extension), imageBytes);

        // send avatar
        new DoCallBack().setValues(null, this, new Object[]{image, jsonObject.toString()}).sendAva();

        finish();
    }
}