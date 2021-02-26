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

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.mcal.qrcode.view.CenteredToolBar;

public class ExceptionActivity extends AppCompatActivity {

    private CenteredToolBar toolbar;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        LayoutParams layoutParams = new LinearLayout.LayoutParams(-2, -2);
        layoutParams.gravity = 17;
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(1);
        linearLayout.setLayoutParams(layoutParams);
        setContentView(linearLayout);

        toolbar = new CenteredToolBar(this);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Application Error");
        //toolbar.setBackgroundColor(Color.parseColor(getString(R.color.colorPrimaryDark)));

        ScrollView sv = new ScrollView(this);
        TextView error = new TextView(this);
        error.setGravity(Gravity.CENTER);
        sv.addView(error);
        linearLayout.addView(sv);
        error.setText(getIntent().getStringExtra("error"));
    }
}
