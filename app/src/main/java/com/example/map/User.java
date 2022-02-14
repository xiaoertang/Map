package com.example.map;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.litepal.crud.DataSupport;

public class User extends DataSupport {
    private String name; //用户名
    private String password; //密码

    public String getPassword2() {
        return password2;
    }

    public void setPassword2(String password2) {
        this.password2 = password2;
    }

    private String password2; //密码
    private String phone; //手机号

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User() {
    }
}