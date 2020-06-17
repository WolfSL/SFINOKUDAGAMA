package com.flexiv.sfino.model;

public class Modal_Batch extends Modal_Item{
    private String BatchNo;
    private double SHI;
    private double RetialPrice;
    private String RepCode;
    private String DisCode;

    public Modal_Batch(){

    }


    public String getBatchNo() {
        return BatchNo;
    }

    public void setBatchNo(String batchNo) {
        BatchNo = batchNo;
    }

    public double getSHI() {
        return SHI;
    }

    public void setSHI(double SHI) {
        this.SHI = SHI;
    }

    public double getRetialPrice() {
        return RetialPrice;
    }

    public void setRetialPrice(double retialPrice) {
        RetialPrice = retialPrice;
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
}
