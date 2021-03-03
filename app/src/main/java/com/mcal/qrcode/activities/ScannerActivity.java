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

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcal.qrcode.R;
import com.mcal.qrcode.data.Person;
import com.mcal.qrcode.data.Preferences;
import com.mcal.qrcode.view.CenteredToolBar;
import com.mcal.qrcode.view.PointsOverlayView;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ScannerActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {

    private CenteredToolBar toolbar;

    private AppCompatTextView txtFirstName;
    private AppCompatTextView txtLastName;
    private AppCompatTextView txtPatronymic;
    private AppCompatTextView txtBirthday;

    private QRCodeReaderView qrCodeReaderView;
    private SwitchCompat flashlightCheckBox;
    private SwitchCompat enableDecodingCheckBox;
    private PointsOverlayView pointsOverlayView;
    private Person person = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        setupToolbar("Сканер");

        qrCodeReaderView = findViewById(R.id.qrdecoderview);

        txtFirstName = findViewById(R.id.firstName);
        txtLastName = findViewById(R.id.lastName);
        txtPatronymic = findViewById(R.id.patronymic);
        txtBirthday = findViewById(R.id.birthday);

        flashlightCheckBox = findViewById(R.id.flashlight_checkbox);
        enableDecodingCheckBox = findViewById(R.id.enable_decoding_checkbox);
        pointsOverlayView = findViewById(R.id.points_overlay_view);

        qrCodeReaderView.setAutofocusInterval(2000L);
        qrCodeReaderView.setOnQRCodeReadListener(this);
        qrCodeReaderView.setBackCamera();
        flashlightCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            qrCodeReaderView.setTorchEnabled(isChecked);
        });
        enableDecodingCheckBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            qrCodeReaderView.setQRDecodingEnabled(isChecked);
        });
        qrCodeReaderView.startCamera();
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.startCamera();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (qrCodeReaderView != null) {
            qrCodeReaderView.stopCamera();
        }
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
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/qrcode/showuser.php").openConnection();
                connection.setDoOutput(true);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                bufferedWriter.write("input_id=" + Preferences.getId());
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

    private void outputResult(@NotNull String result) {
        if (result.startsWith("{")) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            person = gson.fromJson(result, Person.class);
        }
        if (person != null) {
            txtFirstName.setText(person.mFirstName);
            txtLastName.setText(person.mLastName);
            txtPatronymic.setText(person.mPatronymic);
            txtBirthday.setText(person.mBirthday.replace('-', '.'));
            result = "Пользователь найден!";
        } else result = "Пользователь не найден!";
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Preferences.setId(text);
        new ScannerActivity.AsyncNetworkCall().execute();

        pointsOverlayView.setPoints(points);
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