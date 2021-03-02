package com.mcal.qrcode.model;

public class User {
    private String id;
    private String name;
    private String surName;
    private String patronymic;
    private String birthDay;

    public User(String id, String name, String surName, String patronymic, String birthDay) {
        this.id = id;
        this.name = name;
        this.surName = surName;
        this.patronymic = patronymic;
        this.birthDay = birthDay;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurName() {
        return surName;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getBirthDay() {
        return birthDay;
    }
}
