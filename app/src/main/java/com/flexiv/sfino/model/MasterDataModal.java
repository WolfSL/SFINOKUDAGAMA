package com.flexiv.sfino.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MasterDataModal implements Serializable {

    private ArrayList<Area_Modal> Area_Modal;
    private ArrayList<Customer_Modal> Customer_Modal;
    private ArrayList<Modal_Item> Modal_Items;
    private ArrayList<Modal_Batch> modal_Batches_Stock;
    private TBLT_ORDERHED MaxOrder;

    public TBLT_ORDERHED getDefModal() {
        return MaxOrder;
    }

    public void setDefModal(TBLT_ORDERHED MaxOrder) {
        this.MaxOrder = MaxOrder;
    }

    public ArrayList<Modal_Batch> getModal_Batches_Stock() {
        return modal_Batches_Stock;
    }

    public void setModal_Batches_Stock(ArrayList<Modal_Batch> modal_Batches_Stock) {
        this.modal_Batches_Stock = modal_Batches_Stock;
    }

    public ArrayList<Modal_Item> getModal_Items() {
        return Modal_Items;
    }

    public void setModal_Items(ArrayList<Modal_Item> modal_Items) {
        Modal_Items = modal_Items;
    }

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
