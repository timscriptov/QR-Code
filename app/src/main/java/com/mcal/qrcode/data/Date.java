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

public class Date {
    @SerializedName("day")
    public String mDay;
    @SerializedName("month")
    public String mMonth;
    @SerializedName("year")
    public String mYear;

    public Date(String day, String month, String year) {
        mDay = day;
        mMonth = month;
        mYear = year;
    }
}

