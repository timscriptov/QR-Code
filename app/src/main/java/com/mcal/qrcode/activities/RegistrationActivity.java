package com.mcal.qrcode.activities;

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.mcal.qrcode.R;
import com.mcal.qrcode.data.Preferences;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class RegistrationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private AppCompatEditText txtSurname; // Фамилия
    private AppCompatEditText txtName; // Имя
    private AppCompatEditText txtPatronymic; // Отчество
    private AppCompatEditText txtPassword; // Отчество
    private AppCompatButton btnBirthDay;
    private AppCompatButton btnRegDone;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        txtSurname = findViewById(R.id.surname);
        txtName = findViewById(R.id.name);
        txtPatronymic = findViewById(R.id.patronymic);
        btnBirthDay = findViewById(R.id.birthDayButton);
        txtPassword = findViewById(R.id.password);
        btnRegDone = findViewById(R.id.btnRegistration);
    }

    private class AsyncNetworkCall extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (Preferences.getSuccessful()) {
                //Preferences.setRegistered(Preferences.getSuccessful());
                Preferences.setId("");
            } else {
                //Preferences.setRegistered(!Preferences.getSuccessful());
                Preferences.setId(s);
                outputResult(s);
            }
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/registration.php").openConnection();
                connection.setDoOutput(true);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                bufferedWriter.write(getEncodedData());
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


    public void sendData(View view) {
        new AsyncNetworkCall().execute();
    }

    private void outputResult(String result) {
        new AlertDialog.Builder(this)
                .setMessage(result)
                .setPositiveButton("OK", null)
                .show();
    }

    private @NotNull String getEncodedData() {
        String name = txtName.getText().toString();
        String surName = txtSurname.getText().toString();
        String patronymic = txtPatronymic.getText().toString();
        String password = txtPassword.getText().toString();
        return "name=" + name + "&surName=" + surName + "&patronymic=" + patronymic + "&birthday=" + Preferences.getDate() + "&password=" + password;
    }

    public void chooseDate(View view) {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, 2000, 1, 1);
        datePickerDialog.setCancelable(false);
        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        //Preferences.setDate(dayOfMonth + "." + month + "." + year);
        Preferences.setDate(year + "." + month + "." + dayOfMonth);
        btnBirthDay.setText(Preferences.getDate());
    }
}
