package com.flexiv.sfino.model;

public class Modal_Item {
    String ItemCode;
    String Desc;
    String sih;
    String RetPrice;

    double Volume;

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

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

    public Modal_Item(String itemCode, String desc, String sih,double Volume) {
        this.ItemCode = itemCode;
        this.Desc = desc;
        this.sih = sih;
        this.Volume = Volume;
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
