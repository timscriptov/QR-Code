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
package com.mcal.qrcode.data;

import com.google.gson.annotations.SerializedName;

public class Person {
    @SerializedName("id")
    public String mId;
    @SerializedName("login")
    public String mLogin;
    @SerializedName("password")
    public String mPassword;
    @SerializedName("firstName")
    public String mFirstName;
    @SerializedName("lastName")
    public String mLastName;
    @SerializedName("patronymic")
    public String mPatronymic;
    @SerializedName("birthday")
    public String mBirthday;


    public Person(String mId, String mLogin, String mPassword, String mFirstName, String mLastName, String mPatronymic, String mBirthday) {
        this.mId = mId;
        this.mLogin = mLogin;
        this.mPassword = mPassword;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPatronymic = mPatronymic;
        this.mBirthday = mBirthday;
    }
}

