package com.flexiv.sfino.model;

import java.io.Serializable;

public class Customer_Modal implements Serializable {
    private String CusCode;
    private String Discode;
    private String CusName;
    private String AreaCode;
    private int VAT;

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }

    public String getDiscode() {
        return Discode;
    }

    public void setDiscode(String discode) {
        Discode = discode;
    }

    public String getCusName() {
        return CusName;
    }

    public void setCusName(String cusName) {
        CusName = cusName;
    }

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public int getVAT() {
        return VAT;
    }

    public void setVAT(int VAT) {
        this.VAT = VAT;
    }
}
