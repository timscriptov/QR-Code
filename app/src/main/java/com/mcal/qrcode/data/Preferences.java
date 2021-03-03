/*
 * Copyright (C) 2021 Тимашков Иван
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.mcal.qrcode.data;

import com.mcal.qrcode.App;

public class Preferences {
    public static boolean getDayNight() {
        return App.getPreferences().getBoolean("dayNight", false);
    }

    public static void setDayNight(boolean flag) {
        App.getPreferences().edit().putBoolean("dayNight", flag).apply();
    }

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
