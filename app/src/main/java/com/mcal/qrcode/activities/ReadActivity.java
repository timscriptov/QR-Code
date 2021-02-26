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
import android.os.Bundle;
import android.view.MenuItem;
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
import com.mcal.qrcode.utils.Utils;
import com.mcal.qrcode.view.PointsOverlayView;

import org.jetbrains.annotations.NotNull;

public class ReadActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback, QRCodeReaderView.OnQRCodeReadListener {

    private static final int MY_PERMISSION_REQUEST_CAMERA = 0;

    private ViewGroup mainLayout;

    private AppCompatTextView txtSurname;
    private AppCompatTextView txtName;
    private AppCompatTextView txtPatronymic;
    private AppCompatTextView txtDate;
    private AppCompatTextView txtPosition;

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

    // Called when a QR is decoded
    // "text" : the text encoded in QR
    // "points" : points where QR control points are placed
    @SuppressLint("SetTextI18n")
    @Override
    public void onQRCodeRead(String text, PointF[] points) {
        //String jsonScript = "{\"date\":{\"day\":\"15\",\"month\":\"04\",\"year\":\"2000\"},\"id\":\"1234567890\",\"surname\":\"Timashkov\", \"name\":\"Ivan\", \"patronymic\":\"Vladimirovich\", \"position\":\"Developer\"}";

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Person fromJson = gson.fromJson(Utils.strEncrypt(text, 5), Person.class);

        txtSurname.setText(fromJson.mSurname);
        txtName.setText(fromJson.mName);
        txtPatronymic.setText(fromJson.mPatronymic);
        txtDate.setText(fromJson.mDate.mDay + "." + fromJson.mDate.mMonth + "." + fromJson.mDate.mYear);
        txtPosition.setText(fromJson.mPosition);

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
        txtPosition = content.findViewById(R.id.position);


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