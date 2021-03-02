package com.mcal.qrcode.activities;

import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcal.qrcode.R;
import com.mcal.qrcode.data.Person;
import com.mcal.qrcode.data.Preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class LoginActivity extends AppCompatActivity {

    public AppCompatEditText txtNickname;
    public AppCompatEditText txtPassword;
    public AppCompatButton btnLogin;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        txtNickname = findViewById(R.id.nickname);
        txtPassword = findViewById(R.id.password);
        btnLogin = findViewById(R.id.login);
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


        }

        @Override
        protected String doInBackground(Void... voids) {
            String nickname = txtNickname.getText().toString();
            String password = txtPassword.getText().toString();

            try {
                HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/showuser.php").openConnection();
                connection.setDoOutput(true);

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
                bufferedWriter.write("nickname=" + nickname + "&password=" + password);
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
}
