package com.mcal.qrcode.task;

import android.os.AsyncTask;

import com.mcal.qrcode.data.Preferences;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class AsyncNetworkCall extends AsyncTask<Void, Void, String> {

    String mPhp;
    String mData;

    public AsyncNetworkCall(String php, String data) {
        mPhp = php;
        mData = data;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (Preferences.getSuccessful()) {
            //Preferences.setRegistered(Preferences.getSuccessful());
            Preferences.setId("");
        } else {
            //Preferences.setRegistered(!Preferences.getSuccessful());
            Preferences.setId(s);
        }
    }

    @Override
    protected String doInBackground(Void... voids) {
        try {
            HttpsURLConnection connection = (HttpsURLConnection) new URL("https://timscriptov.ru/" + mPhp + ".php").openConnection();
            connection.setDoOutput(true);

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
            bufferedWriter.write(mPhp);
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
