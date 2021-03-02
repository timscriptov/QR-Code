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
    @SerializedName("surName")
    public String mSurname;
    @SerializedName("name")
    public String mName;
    @SerializedName("patronymic")
    public String mPatronymic;
    @SerializedName("birthDay")
    public String mDate;

    public Person(String id, String surname, String name, String patronymic, String date) {
        mId = id;
        mSurname = surname;
        mName = name;
        mPatronymic = patronymic;
        mDate = date;
    }
}

