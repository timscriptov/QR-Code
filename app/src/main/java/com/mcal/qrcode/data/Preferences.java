package com.mcal.qrcode.data;

import com.mcal.qrcode.App;

public class Preferences {
    public static String getId() {
        return App.getPreferences().getString("id", null);
    }

    public static void setId(String flag) {
        App.getPreferences().edit().putString("id", flag).apply();
    }

    public static String getLogin() {
        return App.getPreferences().getString("login", null);
    }

    public static void setLogin(String flag) {
        App.getPreferences().edit().putString("login", flag).apply();
    }

    public static String getPassword() {
        return App.getPreferences().getString("password", null);
    }

    public static void setPassword(String flag) {
        App.getPreferences().edit().putString("password", flag).apply();
    }
}
