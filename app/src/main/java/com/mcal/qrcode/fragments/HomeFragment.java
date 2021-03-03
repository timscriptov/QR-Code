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
package com.mcal.qrcode.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.mcal.qrcode.R;
import com.mcal.qrcode.activities.ProfileActivity;
import com.mcal.qrcode.activities.ScannerActivity;
import com.mcal.qrcode.activities.SigninActivity;
import com.mcal.qrcode.activities.SignupActivity;
import com.mcal.qrcode.data.Preferences;
import com.mcal.qrcode.utils.Utils;

import org.jetbrains.annotations.NotNull;


public class HomeFragment extends Fragment {

    final int RC_SIGNUP = 1;
    final int RC_SIGNIN = 1;
    final int RC_SIGNOUT = 1;
    private AppCompatButton mScanner;
    private AppCompatButton mLogin;
    private AppCompatButton mRegistration;
    private AppCompatButton mProfile;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_main, container, false);

        // Сканер QR-Code
        mScanner = mView.findViewById(R.id.scanner);
        mScanner.setOnClickListener(p1 -> {
            if (Utils.isNetworkAvailable()) {
                startActivity(new Intent(getContext(), ScannerActivity.class));
            } else
                Toast.makeText(getContext(), "Нет Интернет подключения!", Toast.LENGTH_SHORT).show();
        });
        mScanner.setVisibility(Preferences.getId() != null ? View.VISIBLE : View.GONE);

        // Авторизация
        mLogin = mView.findViewById(R.id.signin);
        mLogin.setOnClickListener(p1 -> {
            if (Utils.isNetworkAvailable()) {
                startActivityForResult(new Intent(getContext(), SigninActivity.class), RC_SIGNIN);
            } else
                Toast.makeText(getContext(), "Нет Интернет подключения!", Toast.LENGTH_SHORT).show();
        });
        mLogin.setVisibility(Preferences.getId() != null ? View.GONE : View.VISIBLE);

        // Регистрация
        mRegistration = mView.findViewById(R.id.registration);
        mRegistration.setOnClickListener(p1 -> {
            if (Utils.isNetworkAvailable()) {
                startActivityForResult(new Intent(getContext(), SignupActivity.class), RC_SIGNUP);
            } else
                Toast.makeText(getContext(), "Нет Интернет подключения!", Toast.LENGTH_SHORT).show();
        });
        mRegistration.setVisibility(Preferences.getId() != null ? View.GONE : View.VISIBLE);

        // Профиль/Аккаунт
        mProfile = mView.findViewById(R.id.profile);
        mProfile.setOnClickListener(p1 -> {
            if (Utils.isNetworkAvailable()) {
                startActivityForResult(new Intent(getContext(), ProfileActivity.class), RC_SIGNOUT);
            } else
                Toast.makeText(getContext(), "Нет Интернет подключения!", Toast.LENGTH_SHORT).show();
        });
        mProfile.setVisibility(Preferences.getId() != null ? View.VISIBLE : View.GONE);
        return mView;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (Preferences.getId() != null) {
            mRegistration.setVisibility(View.GONE);
            mLogin.setVisibility(View.GONE);
            mProfile.setVisibility(View.VISIBLE);
            mScanner.setVisibility(View.VISIBLE);
        }

        if (Preferences.getId() == null) {
            mRegistration.setVisibility(View.VISIBLE);
            mLogin.setVisibility(View.VISIBLE);
            mProfile.setVisibility(View.GONE);
            mScanner.setVisibility(View.GONE);
        }
    }

}
