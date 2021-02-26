package com.mcal.qrcode.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;

import com.mcal.qrcode.BuildConfig;

public class Dialogs {
    public static void alert(Context context, String title, String message) {
        AlertDialog dlg = new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Ок", (dialog, which) -> {
                    dialog.dismiss();
                })
                .create();
        dlg.show();
    }

    public static void showMessageOKCancel(Context context, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    public static void showScopedStorageDialog(Context context) {
        AlertDialog dlg = new AlertDialog.Builder(context)
                .setTitle("Изменения в Android 11 R")
                .setMessage("В Android 11 появился новый способ управления файлами под названием \"Scoped Storage\". Вам необходимо предоставить специальное разрешение!")
                .setPositiveButton("Ок", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID));
                    context.startActivity(intent);
                    dialog.dismiss();
                })
                .create();
        dlg.show();
    }
}
