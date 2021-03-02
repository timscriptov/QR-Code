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
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;

import com.dlazaro66.qrcodereaderview.QRCodeReaderView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcal.qrcode.R;
import com.mcal.qrcode.data.Person;
import com.mcal.qrcode.data.Preferences;
import com.mcal.qrcode.view.PointsOverlayView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class ReadActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    private ViewGroup mainLayout;

    private AppCompatTextView txtSurname;
    private AppCompatTextView txtName;
    private AppCompatTextView txtPatronymic;
    private AppCompatTextView txtDate;

    private QRCodeReaderView qrCodeReaderView;
    private SwitchCompat flashlightCheckBox;
    private SwitchCompat enableDecodingCheckBox;
    private PointsOverlayView pointsOverlayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        mainLayout = findViewById(R.id.main_layout);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            initQRCodeReaderView();
        } else {
            requestCameraPermission();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode != MY_PERMISSION_REQUEST_CAMERA) {
            return;
        }

        if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Snackbar.make(mainLayout, "Camera permission was granted.", Snackbar.LENGTH_SHORT).show();
            initQRCodeReaderView();
        } else {
            Snackbar.make(mainLayout, "Camera permission request was denied.", Snackbar.LENGTH_SHORT).show();
        }
    }

    private class AsyncNetworkCall extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Preferences.setJson(s);
            //outputResult(s);

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            Person fromJson = gson.fromJson(Preferences.getJson(), Person.class);

            txtSurname.setText(fromJson.mSurname);
            txtName.setText(fromJson.mName);
            txtPatronymic.setText(fromJson.mPatronymic);
            txtDate.setText(fromJson.mDate.replace('-', '.'));
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/showuser.php").openConnection();
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
                Preferences.setSuccessful(Boolean.parseBoolean(connection.getHeaderField("Action-Success")));
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getLocalizedMessage();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        Preferences.setId(text);
        new ReadActivity.AsyncNetworkCall().execute();


        pointsOverlayView.setPoints(points);
    }

    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            Snackbar.make(mainLayout, "Camera access is required to display the camera preview.", Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(ReadActivity.this, new String[]{
                            Manifest.permission.CAMERA
                    }, MY_PERMISSION_REQUEST_CAMERA);
                }
            }).show();
        } else {
            Snackbar.make(mainLayout, "Permission is not available. Requesting camera permission.", Snackbar.LENGTH_SHORT).show();
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.CAMERA
            }, MY_PERMISSION_REQUEST_CAMERA);
        }
    }


    private void initQRCodeReaderView() {
        View content = getLayoutInflater().inflate(R.layout.content_reader, mainLayout, true);

        qrCodeReaderView = content.findViewById(R.id.qrdecoderview);

        txtSurname = content.findViewById(R.id.surname);
        txtName = content.findViewById(R.id.name);
        txtPatronymic = content.findViewById(R.id.patronymic);
        txtDate = content.findViewById(R.id.date);


        flashlightCheckBox = content.findViewById(R.id.flashlight_checkbox);
        enableDecodingCheckBox = content.findViewById(R.id.enable_decoding_checkbox);
        pointsOverlayView = content.findViewById(R.id.points_overlay_view);

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
}