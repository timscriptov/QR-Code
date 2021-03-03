package com.mcal.qrcode.activities;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.mcal.qrcode.R;
import com.mcal.qrcode.data.Person;
import com.mcal.qrcode.data.Preferences;
import com.mcal.qrcode.view.CenteredToolBar;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class ProfileActivity extends AppCompatActivity {

    private CenteredToolBar toolbar;

    private Snackbar snackbar;

    private AppCompatTextView txtFirstName;
    private AppCompatTextView txtLastName;
    private AppCompatTextView txtPatronymic;
    private AppCompatTextView txtBirthday;

    private Bitmap qrImage;
    private AppCompatImageView imgResult;
    private AppCompatButton btnSave;
    private Person person = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        setupToolbar("Профиль");

        txtFirstName = findViewById(R.id.firstName);
        txtLastName = findViewById(R.id.lastName);
        txtPatronymic = findViewById(R.id.patronymic);
        txtBirthday = findViewById(R.id.birthday);

        imgResult = findViewById(R.id.imgResult);
        btnSave = findViewById(R.id.save);

        new AsyncNetworkCall().execute();

        btnSave.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Внимание")
                    .setMessage("Сохранить изображение?")
                    .setPositiveButton("Да", (dialog, which) -> {
                        saveImage();
                        dialog.dismiss();
                    })
                    .setNegativeButton("Нет", null)
                    .create()
                    .show();
        });
    }

    private class AsyncNetworkCall extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            outputResult(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/qrcode/profile.php").openConnection();
                connection.setDoOutput(true);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                bufferedWriter.write("login=" + Preferences.getLogin() + "&password=" + Preferences.getPassword());
                bufferedWriter.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String responseLine;
                while ((responseLine = reader.readLine()) != null) {
                    response.append(responseLine);
                }
                reader.close();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getLocalizedMessage();
            }
        }
    }

    // Выход из аккаунта
    public void signOut(View view) {
        Preferences.setId(null);
        Preferences.setLogin(null);
        Preferences.setPassword(null);
        setResult(RESULT_OK);
        finish();
    }

    // Вывод результатов
    private void outputResult(@NotNull String result) {
        if (result.startsWith("{")) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            person = gson.fromJson(result, Person.class);

            txtFirstName.setText(person.mFirstName);
            txtLastName.setText(person.mLastName);
            txtPatronymic.setText(person.mPatronymic);
            txtBirthday.setText(person.mBirthday.replace("-", "."));
            generateImage(person.mId);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Генерация QR-Code изображения
    private void generateImage(String id) {
        new Thread(() -> {
            int size = 260;

            Map<EncodeHintType, Object> hintMap = new EnumMap<>(EncodeHintType.class);
            hintMap.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hintMap.put(EncodeHintType.MARGIN, 1);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            try {
                BitMatrix byteMatrix = qrCodeWriter.encode(id, BarcodeFormat.QR_CODE, size,
                        size, hintMap);
                int height = byteMatrix.getHeight();
                int width = byteMatrix.getWidth();
                this.qrImage = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        qrImage.setPixel(x, y, byteMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                    }
                }

                this.runOnUiThread(() -> {
                    this.showImage(this.qrImage);
                });
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }).start();
    }

    // Отображение QR-Code изображения
    private void showImage(Bitmap bitmap) {
        if (bitmap == null) {
            imgResult.setImageResource(android.R.color.transparent);
            qrImage = null;
        } else {
            imgResult.setImageBitmap(bitmap);
        }
    }

    // Сохранение QR-Code изображения
    private void saveImage() {
        if (qrImage == null) {
            Toast.makeText(this, "Изображения нет", Toast.LENGTH_SHORT).show();
            return;
        }

        String fname = "qrcode-" + Calendar.getInstance().getTimeInMillis();
        boolean success = true;
        try {
            String result = MediaStore.Images.Media.insertImage(
                    getContentResolver(),
                    qrImage,
                    fname,
                    "QR-Code Image"
            );
            if (result == null) {
                success = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            success = false;
        }

        if (!success) {
            Toast.makeText(this, "Не удалось сохранить изображение!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Изображение сохранено в галерее!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
