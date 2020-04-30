package com.flexiv.sfino.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MasterDataModal implements Serializable {

    private ArrayList<Area_Modal> Area_Modal;
    private ArrayList<Customer_Modal> Customer_Modal;

    public ArrayList<Area_Modal> getAreaList() {
        return Area_Modal;
    }

    public void setAreaList(ArrayList<Area_Modal> areaList) {
        this.Area_Modal = areaList;
    }

    public ArrayList<Customer_Modal> getCustomerList() {
        return Customer_Modal;
    }

    public void setCustomerList(ArrayList<Customer_Modal> customerList) {
        Customer_Modal = customerList;
    }


}
