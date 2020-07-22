package com.flexiv.sfino.model;

public class Card_cus_area extends Customer_Modal{
    private String txt_code;
    private String txt_name;

    public Card_cus_area(String txt_code, String txt_name) {
        this.txt_code = txt_code;
        this.txt_name = txt_name;
    }

    public String getTxt_code() {
        return txt_code;
    }

    public void setTxt_code(String txt_code) {
        this.txt_code = txt_code;
    }

    public String getTxt_name() {
        return txt_name;
    }

    public void setTxt_name(String txt_name) {
        this.txt_name = txt_name;
    }
}
