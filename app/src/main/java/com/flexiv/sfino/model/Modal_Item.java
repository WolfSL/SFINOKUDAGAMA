package com.flexiv.sfino.model;

public class Modal_Item {
    String ItemCode;
    double Extra;
    String Desc;


    public Modal_Item(String itemCode, double extra, String desc) {
        ItemCode = itemCode;
        Extra = extra;
        Desc = desc;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }


    public double getExtra() {
        return Extra;
    }

    public void setExtra(double extra) {
        Extra = extra;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }
}
