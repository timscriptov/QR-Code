package com.mcal.qrcode.activities;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.mcal.qrcode.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

public class RegistrationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private AppCompatEditText txtSurname; // Фамилия
    private AppCompatEditText txtName; // Имя
    private AppCompatEditText txtPatronymic; // Отчество
    private AppCompatButton birthDay;
    private AppCompatButton btnRegDone;
    private String date;
    private Handler handler = new Handler();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtSurname = findViewById(R.id.surname);
        txtName = findViewById(R.id.name);
        txtPatronymic = findViewById(R.id.patronymic);
        birthDay = findViewById(R.id.birthDayButton);
        btnRegDone = findViewById(R.id.btnRegistration);
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
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/registration.php").openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                bufferedWriter.write(getEncodedData());
                bufferedWriter.close();

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer response = new StringBuffer();
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


    public void sendData(View view) {
        new AsyncNetworkCall().execute();
    }

    private void outputResult(String result) {
        new AlertDialog.Builder(this)
                .setMessage(result)
                .setPositiveButton("OK", null)
                .show();
    }

    private String getEncodedData() {
        String name = txtName.getText().toString();
        String surName = txtSurname.getText().toString();
        String patronymic = txtPatronymic.getText().toString();
        try {
            return URLEncoder.encode("name=" + name + "&surName=" + surName + "&patronymic=" + patronymic + "&birthDay=" + birthDay, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void chooseDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, 2000, 1, 1);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = dayOfMonth + "." + month + "." + year;
        birthDay.setText(date);
    }
}
