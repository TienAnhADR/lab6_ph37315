package com.example.asm_anhntph37315.model;

import java.io.Serializable;

public class Product implements Serializable {
    private String _id;
    private String namePro;
    private String image;
    private double pricePro;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNamePro() {
        return namePro;
    }

    public void setNamePro(String namePro) {
        this.namePro = namePro;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getPricePro() {
        return pricePro;
    }

    public void setPricePro(double pricePro) {
        this.pricePro = pricePro;
    }

    public Product() {
    }

    public Product(String namePro, String image, double pricePro) {
        this.namePro = namePro;
        this.image = image;
        this.pricePro = pricePro;
    }

    public Product(String _id, String namePro, String image, double pricePro) {
        this._id = _id;
        this.namePro = namePro;
        this.image = image;
        this.pricePro = pricePro;
    }

    @Override
    public String toString() {
        return "Product{" +
                "_id='" + _id + '\'' +
                ", namePro='" + namePro + '\'' +
                ", image='" + image + '\'' +
                ", pricePro=" + pricePro +
                '}';
    }
}
