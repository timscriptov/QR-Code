package com.mcal.qrcode.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import javax.net.ssl.HttpsURLConnection;

public class SigninActivity extends AppCompatActivity {

    private CenteredToolBar toolbar;

    public AppCompatEditText txtLogin;
    public AppCompatEditText txtPassword;
    public AppCompatButton btnSignin;

    private Person person = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        setupToolbar("Авторизация");

        txtLogin = findViewById(R.id.login);
        txtPassword = findViewById(R.id.password);
        btnSignin = findViewById(R.id.signin);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void sendData(View view) {
        new AsyncNetworkCall().execute();
    }

    private class AsyncNetworkCall extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            outputResult(s);
        }

        @Override
        protected String doInBackground(Void... voids) {
            String login = txtLogin.getText().toString();
            String password = txtPassword.getText().toString();

            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/qrcode/signin.php").openConnection();
                connection.setDoOutput(true);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                bufferedWriter.write("login=" + login + "&password=" + password);
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

    // Вывод результатов
    private void outputResult(@NotNull String result) {
        if (result.startsWith("{")) {
            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            person = gson.fromJson(result, Person.class);
        }
        if (person != null) {
            Preferences.setId(person.mId);
            Preferences.setLogin(person.mLogin);
            Preferences.setPassword(person.mPassword);
            result = "Вход выполнен!";
            setResult(RESULT_OK);
            finish();
        } else {
            setResult(RESULT_CANCELED);
        }
        Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
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
