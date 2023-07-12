package com.example.instagram.services;

import androidx.annotation.NonNull;

public enum CacheScopes {
    MAIN_SCOPE("ClickShot"),
    MEDIA_PERMISSION("MediaBookPermission"), USER_TOKEN("UserToken"), USER_LOGIN("UserLogin"), USER_PASSWORD("UserPassword"), USER_PASSWORD_REPEAT("UserPasswordRepeat"), // TODO delete finally
    USER_IS_RECEIVE_LETTERS("ReceiveNewsletters"), USER_IS_HIDE_EMAIL("HideEmail"), USER_IP("UserIp"), USER_EMAIL("UserEmail"), USER_PREFER_THEME("PreferTheme"), USER_EMAIL_CODE("EmailCode"), // TODO delete finally
    REMEMBER_ME("RememberMe"), SELF_PAGE_USER_LOGIN("selfPageUserLogin"), LAST_THEME("lastTheme"), IS_SUBSCRIBED("isMeSubscribed");
    private final String text;

    CacheScopes(final String text) {
        this.text = text;
    }

    @NonNull
    @Override
    public String toString() {
        return text;
    }
}
