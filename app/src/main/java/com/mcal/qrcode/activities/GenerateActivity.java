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
package com.mcal.qrcode.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mcal.qrcode.R;
import com.mcal.qrcode.data.Date;
import com.mcal.qrcode.data.Person;
import com.mcal.qrcode.ui.Dialogs;
import com.mcal.qrcode.utils.Utils;

import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;

public class GenerateActivity extends AppCompatActivity {
    private final String TAG = "GenerateActivity.java";
    private final int REQUEST_PERMISSION = 0xf0;

    private GenerateActivity self;
    private Snackbar snackbar;
    private Bitmap qrImage;

    private AppCompatEditText txtSurname;
    private AppCompatEditText txtName;
    private AppCompatEditText txtPatronymic;
    private AppCompatEditText txtDay;
    private AppCompatEditText txtMonth;
    private AppCompatEditText txtYear;
    private AppCompatEditText txtPosition;

    private AppCompatTextView txtSaveHint;
    private AppCompatButton btnGenerate, btnReset;
    private AppCompatImageView imgResult;
    private ProgressBar loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        self = this;

        txtSurname = findViewById(R.id.surname);
        txtName = findViewById(R.id.name);
        txtPatronymic = findViewById(R.id.patronymic);
        txtDay = findViewById(R.id.day);
        txtMonth = findViewById(R.id.month);
        txtYear = findViewById(R.id.year);
        txtPosition = findViewById(R.id.position);

        txtSaveHint = findViewById(R.id.txtSaveHint);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnReset = findViewById(R.id.btnReset);
        imgResult = findViewById(R.id.imgResult);
        loader = findViewById(R.id.loader);

        btnGenerate.setOnClickListener(v -> {
            self.generateImage();
        });

        btnReset.setOnClickListener(v -> {
            self.reset();
        });

        imgResult.setOnClickListener(v -> {
            self.confirm("Сохранить изображение?", "Да", (dialog, which) -> {
                saveImage();
            });
        });
    }

    @Override
    public void onRequestPermissionsResult(final int requestCode, @NonNull final String[] permissions, @NonNull final int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveImage();
            } else {
                Dialogs.alert(self, "QRCode Generator", "У приложения нет доступа для сохранения изображений");
            }
        }
    }

    public String jsonGenerate() {

        Gson gson = new Gson();

        Date date = new Date(txtDay.getText().toString(), txtMonth.getText().toString(), txtYear.getText().toString());

        Person person = new Person(
                Utils.getRandomString(8),
                txtSurname.getText().toString(),
                txtName.getText().toString(),
                txtPatronymic.getText().toString(),
                date,
                txtPosition.getText().toString());

        String json = gson.toJson(person);

        Dialogs.alert(self, "QRCode Generator", Utils.strEncrypt(json, 5));

        return Utils.strEncrypt(json, 5);
    }

    private void saveImage() {
        if (qrImage == null) {
            Dialogs.alert(self, "QRCode Generator", "Изображения нет");
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_PERMISSION);
            return;
        }


        String fname = "qrcode-" + Calendar.getInstance().getTimeInMillis();
        boolean success = true;
        try {
            String result = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    qrImage,
                    fname,
                    "QRCode Image"
            );
            if (result == null) {
                success = false;
            } else {
                Log.e(TAG, result);
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        if (!success) {
            Dialogs.alert(self, "QRCode Generator", "Не удалось сохранить изображение");
        } else {
            self.snackbar("Изображение сохранено в галерее");
        }
    }

    private void confirm(String msg, String yesText, final AlertDialog.OnClickListener yesListener) {
        AlertDialog dlg = new AlertDialog.Builder(self)
                .setTitle("Внимание")
                .setMessage(msg)
                .setNegativeButton("Отмена", (dialog, which) -> {
                    dialog.dismiss();
                })
                .setPositiveButton(yesText, yesListener)
                .create();
        dlg.show();
    }

    private void snackbar(String msg) {
        if (self.snackbar != null) {
            self.snackbar.dismiss();
        }

        self.snackbar = Snackbar.make(
                findViewById(R.id.mainBody),
                msg, Snackbar.LENGTH_SHORT);

        self.snackbar.show();
    }

    private void endEditing() {
        txtSurname.clearFocus();
        txtName.clearFocus();
        txtPatronymic.clearFocus();
        txtDay.clearFocus();
        txtMonth.clearFocus();
        txtYear.clearFocus();
        txtPosition.clearFocus();
        //InputMethodManager imm = (InputMethodManager)getSystemService(Context.
        //        INPUT_METHOD_SERVICE);
        //imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
    }


    private void generateImage() {
        final String text = jsonGenerate();
        if (text.trim().isEmpty()) {
            Dialogs.alert(self, "QRCode Generator", "Сначала введите данные, чтобы создать QR-код.");
            return;
        }

        endEditing();
        showLoadingVisible(true);
        new Thread(() -> {
            int size = imgResult.getMeasuredWidth();
            if (size > 1) {
                Log.e(TAG, "size is set manually");
                size = 260;
            }

            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintMap.put(EncodeHintType.MARGIN, 1);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                BitMatrix byteMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, size,
                        size, hintMap);
                int height = byteMatrix.getHeight();
                int width = byteMatrix.getWidth();
                self.qrImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        qrImage.setPixel(x, y, byteMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

                self.runOnUiThread(() -> {
                    self.showImage(self.qrImage);
                    self.showLoadingVisible(false);
                    self.snackbar("QRCode создан");
                });
            } catch (WriterException e) {
                e.printStackTrace();
                Dialogs.alert(self, "QRCode Generator", e.getMessage());
            }
        }).start();
    }

    private void showLoadingVisible(boolean visible) {
        if (visible) {
            showImage(null);
        }

        loader.setVisibility(
                (visible) ? View.VISIBLE : View.GONE
        );
    }

    private void reset() {
        txtSurname.setText("");
        txtName.setText("");
        txtPatronymic.setText("");
        txtDay.setText("");
        txtMonth.setText("");
        txtYear.setText("");
        txtPosition.setText("");
        showImage(null);
        endEditing();
    }

    private void showImage(Bitmap bitmap) {
        if (bitmap == null) {
            imgResult.setImageResource(android.R.color.transparent);
            qrImage = null;
            txtSaveHint.setVisibility(View.GONE);
        } else {
            imgResult.setImageBitmap(bitmap);
            txtSaveHint.setVisibility(View.VISIBLE);
        }
    }
}