package com.flexiv.sfino.model;

public class Modal_Batch extends Modal_Item{
    private String code;
    private double sih;
    private double price;

    public Modal_Batch(String itemCode, double extra, String desc, String code, double sih, double price) {
        super(itemCode, extra, desc);
        this.code = code;
        this.sih = sih;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getSih() {
        return sih;
    }

    public void setSih(double sih) {
        this.sih = sih;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
