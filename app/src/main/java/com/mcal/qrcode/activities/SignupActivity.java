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

import android.app.DatePickerDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcal.qrcode.R;
import com.mcal.qrcode.data.Preferences;
import com.mcal.qrcode.data.Users;
import com.mcal.qrcode.view.CenteredToolBar;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class SignupActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private CenteredToolBar toolbar;

    private AppCompatEditText txtFirstName; // Фамилия
    private AppCompatEditText txtLastName; // Имя
    private AppCompatEditText txtPatronymic; // Отчество
    private AppCompatEditText txtPassword; // Пароль
    private AppCompatEditText txtLogin; // Логин

    private AppCompatButton btnBirthDay;
    private AppCompatButton btnSignup;
    private String date = "";
    private Users users = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        setupToolbar("Регистрация");

        txtFirstName = findViewById(R.id.firstName);
        txtLastName = findViewById(R.id.lastName);
        txtPatronymic = findViewById(R.id.patronymic);
        btnBirthDay = findViewById(R.id.birthday);
        txtPassword = findViewById(R.id.password);
        txtLogin = findViewById(R.id.login);
        btnSignup = findViewById(R.id.signup);
        btnSignup.setOnClickListener(p1 -> {
            new AsyncNetworkCall().execute();
        });
        btnBirthDay.setOnClickListener(p1 -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(this, this, 2000, 1, 1);
            datePickerDialog.setCancelable(false);
            datePickerDialog.show();
        });
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    // Вывод результатов
    private void outputResult(@NotNull String result) {
        if (result.startsWith("{")) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            users = gson.fromJson(result, Users.class);
        }
        if (users != null) {
            Preferences.setId(users.mId);
            Preferences.setLogin(users.mLogin);
            Preferences.setPassword(users.mPassword);
            result = "Регистрация прошла успешно!";
            setResult(RESULT_OK);
            finish();
        } else {
            setResult(RESULT_CANCELED);
        }
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
    }

    // Генерация запроса
    private @NotNull String getEncodedData() {
        String login = txtLogin.getText().toString();
        String password = txtPassword.getText().toString();
        String firstName = txtFirstName.getText().toString();
        String lastName = txtLastName.getText().toString();
        String patronymic = txtPatronymic.getText().toString();

        return "login=" + login + "&password=" + password + "&firstName=" + firstName + "&lastName=" + lastName + "&patronymic=" + patronymic + "&birthday=" + date;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        date = year + "." + month + "." + dayOfMonth;
        btnBirthDay.setText(date);
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

    private class AsyncNetworkCall extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            outputResult(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/qrcode/signup.php").openConnection();
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
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
                return "Error: " + e.getLocalizedMessage();
            }
        }
    }
}
