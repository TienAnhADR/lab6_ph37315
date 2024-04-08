package com.example.asm_anhntph37315.model;

public class KqRes {
    private Boolean success;
    private String message;
    private User data;

    public KqRes(Boolean success, String message, User data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    public KqRes() {
    }

    public KqRes(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
