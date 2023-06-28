package com.example.instagram.DAOs;

import com.example.instagram.services.DateFormatting;
import com.example.instagram.services.TransitUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

public class User {
    // region getters
    public String getToken() {
        return token;
    }

    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    public String getLogin() {
        return login;
    }

    public String getSurname() {
        return surname;
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
    public int getAmountPosts() {
        return amountPosts;
    }

    public int getAmountSubscribers() {
        return amountSubscribers;
    }

    public int getAmountSubscribing() {
        return amountSubscribing;
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

    public String getEmailCode() {
        return emailCode;
    }
    public boolean isEmailConfirmed() {
        return isEmailConfirmed;
    }

    // endregion
    // region setters
    public void setToken(String token) {
        this.token = token;
    }

    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLogin(String name) {
        this.login = name;
    }
    public void setAmountPosts(int amountPosts) {
        this.amountPosts = amountPosts;
    }

    public void setAmountSubscribers(int amountSubscribers) {
        this.amountSubscribers = amountSubscribers;
    }

    public void setAmountSubscribing(int amountSubscribing) {
        this.amountSubscribing = amountSubscribing;
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

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setOtherInfo(UserOtherInfo otherInfo) {
        this.otherInfo = otherInfo;
    }

    public void setEmailConfirmed(boolean emailConfirmed) {
        isEmailConfirmed = emailConfirmed;
    }

    public void setEmailCode(String emailCode) {
        this.emailCode = emailCode;
    }

    // endregion
    private String login;
    private String nickName;

    private String surname;
    private String emailCode;
    private String phoneNumber = "";
    private String password;
    private String passwordRepeat;
    private String email = "";
    private Date birthday;
    private String avatar;
    private String token;
    private String description = "";
    private int amountPosts;
    private int amountSubscribers;
    private int amountSubscribing;

    private boolean isEmailConfirmed;

    private UserOtherInfo otherInfo = new UserOtherInfo();

    public User() {
        otherInfo.setDateOfRegistration(new Date());
        otherInfo.setLastOnline(new Date());
        otherInfo.setMacAddress("MAC_ADDRESS");
        otherInfo.setPreferInterfaceLanguage(Locale.getDefault().getLanguage());
    }

    public User(String login, String nickName, String phoneNumber, String password, String email) {
        this.login = login;
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.email = email;
    }

    public User(JSONObject object) throws JSONException {
        setLogin(object.getString("login"));
        setNickName(object.getString("name"));
    }

    // could be changed
    public JSONObject getJSON() throws JSONException {

        JSONObject userBody = new JSONObject();
        userBody.put("login", login);
        userBody.put("password", password);
        userBody.put("name", login);
        userBody.put("surname", "");
        userBody.put("phone", phoneNumber);
        userBody.put("email", email);
        userBody.put("avatar", "avatar");
        userBody.put("birthday", DateFormatting.formatToDateWithoutTime(birthday));
        userBody.put("bio", description);

        return userBody;
    }

    public JSONObject getJSONToCheck() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", login);
        jsonObject.put("password", password);

        return jsonObject;
    }

    public JSONObject getJSONLogin() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", login);

        return jsonObject;
    }

    public JSONObject getJSONAfterForgotPassword() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("login", login);
        jsonObject.put("code", emailCode);
        jsonObject.put("newPassword", password);
        jsonObject.put("repeatPassword", passwordRepeat);

        return jsonObject;
    }

    public static User getPublicUser(JSONObject user, String login) throws JSONException, ParseException {
        User selfPageUser = new User();

        selfPageUser.setLogin(login);
        selfPageUser.setAmountPosts(user.getInt("postsAmount"));
        selfPageUser.setSurname(user.getString("surname"));
        selfPageUser.setNickName(user.getString("name"));
        selfPageUser.setEmail(user.getString("email"));
        selfPageUser.setDescription(user.getString("bio"));
        Date date = DateFormatting.formatDateFromStandard(user.getString("birthday"));
        selfPageUser.setBirthday(date);

        // TODO add birthday, subscribing, subscribers
        return selfPageUser;
    }

    public JSONObject getToChange() throws JSONException {
        JSONObject user = new JSONObject();

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", nickName);
        jsonObject.put("surname", surname);
        jsonObject.put("bio", description);
        jsonObject.put("birthday", DateFormatting.formatToDateWithTime(birthday));

        user.put("token", TransitUser.user.token);
        user.put("user", jsonObject);

        return user;
    }
}
