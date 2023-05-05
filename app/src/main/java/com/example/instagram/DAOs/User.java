package com.example.instagram.DAOs;

import com.example.instagram.services.DateFormatting;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class User {
    // region getters
    public String getName() {
        return name;
    }

    public String getNickName() {
        return nickName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public Date getBirthday() {
        return birthday;
    }

    public String getDescription() {
        return description;
    }

    public String getAvatar() {
        return avatar;
    }

    public UserOtherInfo getOtherInfo() {
        return otherInfo;
    }


    // endregion
    // region setters
    public void setDescription(String description) {
        this.description = description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setOtherInfo(UserOtherInfo otherInfo) {
        this.otherInfo = otherInfo;
    }

    // endregion
    private String name;
    private String nickName;
    private String phoneNumber = "NO_PHONE";
    private String password;
    private String email = "empty@i.ua";
    private Date birthday;
    private String avatar;
    private String description = "NO_BIO";
    private List<User> subscribers;
    private List<User> subscribings;
    private UserOtherInfo otherInfo = new UserOtherInfo();

    public User() {
        otherInfo.setDateOfRegistration(new Date());
        otherInfo.setLastOnline(new Date());
        otherInfo.setMacAddress("MAC_ADDRESS");
        otherInfo.setPreferInterfaceLanguage(Locale.getDefault().getLanguage());
    }

    public User(String name, String nickName, String phoneNumber, String password, String email) {
        this.name = name;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
    }

    // TODO: json constructor

    // could be changed
    public JSONObject getJSON() throws JSONException {

        JSONObject userBody = new JSONObject();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", name);
        jsonObject.put("password", password);
        jsonObject.put("name", name);
        jsonObject.put("surname", name);
        jsonObject.put("phone", phoneNumber);
        jsonObject.put("email", email);
        jsonObject.put("avatar", "avatar");
        jsonObject.put("birthday", DateFormatting.formatToGeneralDate(birthday));
        jsonObject.put("bio", description);

        userBody.put("userBody", jsonObject);

        return userBody;
    }

    public JSONObject getJSONToCheck() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", nickName);
        jsonObject.put("password", password);

        return jsonObject;
    }
}
