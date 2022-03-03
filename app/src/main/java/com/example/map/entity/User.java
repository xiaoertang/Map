package com.example.map.entity;

import org.litepal.crud.DataSupport;

public class User extends DataSupport {
    private String name; //用户名
    private String password; //密码
    private String status; //用户状态：0未登录，1已登录
    private String phone; //手机号

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

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