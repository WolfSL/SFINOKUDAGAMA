package com.flexiv.sfino.model;

import java.io.Serializable;
import java.util.ArrayList;

public class MasterDataModal implements Serializable {

    private ArrayList<Area_Modal> Area_Modal;
    private ArrayList<Customer_Modal> Customer_Modal;
    private ArrayList<Modal_Item> Modal_Items;
    private ArrayList<Modal_Batch> modal_Batches_Stock;
    private ArrayList<Modal_RepStock> modal_Rep_Stock;
    private ArrayList<TBLM_BANK> modal_banks;
    private TBLT_ORDERHED MaxOrder;
    private TBLT_SALINVHED MaxInv;

    public ArrayList<TBLM_BANK> getModal_banks() {
        return modal_banks;
    }

    public void setModal_banks(ArrayList<TBLM_BANK> modal_banks) {
        this.modal_banks = modal_banks;
    }

    public ArrayList<Modal_RepStock> getModal_Rep_Stock() {
        return modal_Rep_Stock;
    }

    public void setModal_Rep_Stock(ArrayList<Modal_RepStock> modal_Rep_Stock) {
        this.modal_Rep_Stock = modal_Rep_Stock;
    }

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

    public TBLT_SALINVHED getMaxInv() {
        return MaxInv;
    }

    public void setMaxInv(TBLT_SALINVHED maxInv) {
        MaxInv = maxInv;
    }
}
