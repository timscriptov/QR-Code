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
package com.mcal.qrcode.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mcal.qrcode.App;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class Utils {
    // Проверка Интернет подключения
    public static boolean isNetworkAvailable() {
        ConnectivityManager connection = (ConnectivityManager) App.getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connection.getActiveNetworkInfo();
        if (info == null) return false;
        else return info.isConnected();
    }

    // Генерация рандомной строки
    public static @NotNull String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    // Хор шифрование строки
    public static @NotNull String strEncrypt(String str, int i) {
        try {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i2 = 0; i2 < str.length(); i2++) {
                stringBuilder.append((char) (str.charAt(i2) ^ strSymbols(i)[i2 % strSymbols(i).length]));
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            return "";
        }
    }

    @Contract(value = "_ -> new", pure = true)
    public static char @NotNull [] strSymbols(int i) {
        switch (i) {
            case 0:
                return new char[]{'鉝'};
            case 1:
                return new char[]{'々'};
            case 2:
                return new char[]{'〆'};
            case 3:
                return new char[]{'\u0670'};
            case 4:
                return new char[]{'ۖ'};
            case 5:
                return new char[]{'A'};
            default:
                return new char[0];
        }
    }
}
