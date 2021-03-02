package com.mcal.qrcode.data;

import com.mcal.qrcode.App;

public class Preferences {

    public static boolean getSuccessful() {
        return App.getPreferences().getBoolean("isSuccessful", false);
    }

    public static void setSuccessful(boolean flag) {
        App.getPreferences().edit().putBoolean("isSuccessful", flag).apply();
    }

    public static boolean getRegistered() {
        return App.getPreferences().getBoolean("isRegistered", false);
    }

    public static void setRegistered(boolean flag) {
        App.getPreferences().edit().putBoolean("isRegistered", flag).apply();
    }

    public static String getId() {
        return App.getPreferences().getString("userId", "");
    }

    public static void setId(String flag) {
        App.getPreferences().edit().putString("userId", flag).apply();
    }

    public static String getDate() {
        return App.getPreferences().getString("birthDay", "");
    }


    public static void setDate(String flag) {
        App.getPreferences().edit().putString("birthDay", flag).apply();
    }

    public static void setJson(String flag) {
        App.getPreferences().edit().putString("json", flag).apply();
    }

    public static String getJson() {
        return App.getPreferences().getString("json", "");
    }
}
