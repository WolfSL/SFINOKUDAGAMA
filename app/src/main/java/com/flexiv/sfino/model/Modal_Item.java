package com.flexiv.sfino.model;

public class Modal_Item {
    String ItemCode;
    String Desc;
    String sih;
    String RetPrice;

    public String getRetPrice() {
        return RetPrice;
    }

    public void setRetPrice(String retPrice) {
        RetPrice = retPrice;
    }

    public String getSih() {
        return sih;
    }

    public void setSih(String sih) {
        this.sih = sih;
    }

    public Modal_Item(){

    }

    public Modal_Item(String itemCode, String desc, String sih) {
        ItemCode = itemCode;
        Desc = desc;
        this.sih = sih;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }


    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }
}
