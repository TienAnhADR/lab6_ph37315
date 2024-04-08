package com.example.asm_anhntph37315.model;

import java.util.List;

public class KqResProduct {
    private boolean success;
    private String message;
    private List<Product> data;

    public KqResProduct() {
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Product> getData() {
        return data;
    }

    public void setData(List<Product> data) {
        this.data = data;
    }

    public KqResProduct(boolean success, String message, List<Product> data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    @Override
    public String toString() {
        return "KqResProduct{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
