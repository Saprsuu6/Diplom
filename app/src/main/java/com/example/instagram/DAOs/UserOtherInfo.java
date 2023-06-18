package com.example.instagram.DAOs;

import java.util.Date;

public class UserOtherInfo {
    // region setters
    public void setToken(String token) {
        this.token = token;
    }
    public void setPhoneBookPermission(boolean phoneBookPermission) {
        this.phoneBookPermission = phoneBookPermission;
    }

    public void setMediaPermission(boolean mediaPermission) {
        this.mediaPermission = mediaPermission;
    }

    public void setDateOfRegistration(Date dateOfRegistration) {
        this.dateOfRegistration = dateOfRegistration;
    }

    public void setLastOnline(Date lastOnline) {
        this.lastOnline = lastOnline;
    }

    public void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public void setMailVerificationCode(String mailVerificationCode) {
        this.mailVerificationCode = mailVerificationCode;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public void setPreferInterfaceLanguage(String preferInterfaceLanguage) {
        this.preferInterfaceLanguage = preferInterfaceLanguage;
    }

    public void setPreferInterfaceTheme(int preferInterfaceTheme) {
        this.preferInterfaceTheme = preferInterfaceTheme;
    }

    public void setReceiveNewsletters(boolean receiveNewsLetters) {
        this.receiveNewsLetters = receiveNewsLetters;
    }

    public void setHidePhone(boolean hidePhone) {
        this.hidePhone = hidePhone;
    }

    public void setHideEmail(boolean hideEmail) {
        this.hideEmail = hideEmail;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }


    public void setPreferBackground(int preferBackground) {
        this.preferBackground = preferBackground;
    }

    public void setReceiveNewsLetters(boolean receiveNewsLetters) {
        this.receiveNewsLetters = receiveNewsLetters;
    }

    // endregion
    // region getters
    public String getToken() {
        return token;
    }
    public Date getDateOfRegistration() {
        return dateOfRegistration;
    }

    public Date getLastOnline() {
        return lastOnline;
    }

    public String getMacAddress() {
        return macAddress;
    }

    public String getMailVerificationCode() {
        return mailVerificationCode;
    }

    public boolean isBanned() {
        return isBanned;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public String getPreferInterfaceLanguage() {
        return preferInterfaceLanguage;
    }

    public int getPreferInterfaceTheme() {
        return preferInterfaceTheme;
    }

    public boolean isReceiveNewsletters() {
        return receiveNewsLetters;
    }

    public boolean isHidePhone() {
        return hidePhone;
    }

    public boolean isHideEmail() {
        return hideEmail;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public boolean isPhoneBookPermission() {
        return phoneBookPermission;
    }

    public boolean isMediaPermission() {
        return mediaPermission;
    }

    public int getPreferBackground() {
        return preferBackground;
    }

    public boolean isReceiveNewsLetters() {
        return receiveNewsLetters;
    }
    // endregion

    private Date dateOfRegistration;
    private Date lastOnline;
    private String macAddress;
    private String ipAddress;
    private String mailVerificationCode;
    private boolean isBanned = false;
    private boolean isPremium = false;
    private String preferInterfaceLanguage;
    private int preferInterfaceTheme;
    private int preferBackground;
    private boolean receiveNewsLetters;
    private boolean hidePhone;
    private boolean hideEmail;
    private boolean phoneBookPermission;
    private boolean mediaPermission;
    private String token;
// json constructor
}
