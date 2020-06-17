package com.flexiv.sfino.model;

public class Modal_Item {
    String ItemCode;
    String Desc;


    public Modal_Item(){

    }

    public Modal_Item(String itemCode, String desc) {
        ItemCode = itemCode;
        Desc = desc;
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
