package com.example.todolistapp.Model;

public class UserModel {
    private int user_id, user_status;
    private String name;

    public int getUserId() {
        return user_id;
    }

    public void setUserId(int user_id) {
        this.user_id = user_id;
    }

    public int getUserStatus() {
        return user_status;
    }

    public void setUserStatus(int user_status) {
        this.user_status = user_status;
    }

    public String getUserName() {
        return name;
    }

    public void setUserName(String name) {
        this.name = name;
    }
}
