package com.flexiv.sfino.model;

public class Modal_RepStock {
    private String RepCode;
    private String DisCode;
    private String BatchNo;
    private String ItemCode;
    private double SIH;
    private double FreeQty;
    private String Status;
    private double RetialPrice;
    private String ExpDate;
    private String TourID;
    private String Createdate;

    public String getCreatedate() {
        return Createdate;
    }

    public void setCreatedate(String createdate) {
        Createdate = createdate;
    }

    public String getExpDate() {
        return ExpDate;
    }

    public void setExpDate(String expDate) {
        ExpDate = expDate;
    }

    public String getTourID() {
        return TourID;
    }

    public void setTourID(String tourID) {
        TourID = tourID;
    }

    public String getRepCode() {
        return RepCode;
    }

    public void setRepCode(String repCode) {
        RepCode = repCode;
    }

    public String getDisCode() {
        return DisCode;
    }

    public void setDisCode(String disCode) {
        DisCode = disCode;
    }

    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public double getSIH() {
        return SIH;
    }

    public void setSIH(double SIH) {
        this.SIH = SIH;
    }

    public double getFreeQty() {
        return FreeQty;
    }

    public void setFreeQty(double freeQty) {
        FreeQty = freeQty;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public double getRetialPrice() {
        return RetialPrice;
    }

    public void setRetialPrice(double retialPrice) {
        RetialPrice = retialPrice;
    }
}
