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

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.mcal.qrcode.R;
import com.mcal.qrcode.activities.GenerateActivity;
import com.mcal.qrcode.activities.ReadActivity;
import com.mcal.qrcode.activities.RegistrationActivity;
import com.mcal.qrcode.data.Preferences;

import org.jetbrains.annotations.NotNull;


public class HomeFragment extends Fragment {

    private AppCompatButton mGenerateQRCode;
    private AppCompatButton mReadQRCode;
    private AppCompatButton mLogin;
    private AppCompatButton mRegistration;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_main, container, false);

        mGenerateQRCode = mView.findViewById(R.id.qrgenerate);
        mReadQRCode = mView.findViewById(R.id.qrread);
        mLogin = mView.findViewById(R.id.login);
        mRegistration = mView.findViewById(R.id.registration);

        mLogin.setOnClickListener(p1 -> startActivity(new Intent(getContext(), RegistrationActivity.class)));
        mLogin.setVisibility(Preferences.getRegistered() ? View.GONE : View.VISIBLE);

        mRegistration.setOnClickListener(p1 -> startActivity(new Intent(getContext(), RegistrationActivity.class)));
        mRegistration.setVisibility(Preferences.getRegistered() ? View.GONE : View.VISIBLE);

        mGenerateQRCode.setOnClickListener(p1 -> startActivity(new Intent(getContext(), GenerateActivity.class)));
        mReadQRCode.setOnClickListener(p1 -> startActivity(new Intent(getContext(), ReadActivity.class)));

        return mView;
    }
}
