package com.mcal.qrcode.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import com.mcal.qrcode.R;
import com.mcal.qrcode.activities.GenerateActivity;
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
import com.mcal.qrcode.activities.ReadActivity;
import com.mcal.qrcode.activities.RegistrationActivity;

import org.jetbrains.annotations.NotNull;

public class HomeFragment extends Fragment {

    public AppCompatButton mGenerateQRCode;
    public AppCompatButton mReadQRCode;
    public AppCompatButton mRegistration;
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.activity_main, container, false);

        mGenerateQRCode = mView.findViewById(R.id.qrgenerate);
        mReadQRCode = mView.findViewById(R.id.qrread);

        (mView.findViewById(R.id.registration)).setOnClickListener(p1 -> {
            startActivity(new Intent(getContext(), RegistrationActivity.class));
        });

        (mView.findViewById(R.id.qrgenerate)).setOnClickListener(p1 -> {
            startActivity(new Intent(getContext(), GenerateActivity.class));
        });

        (mView.findViewById(R.id.qrread)).setOnClickListener(p1 -> {
            startActivity(new Intent(getContext(), ReadActivity.class));
        });

        return mView;
    }
}
