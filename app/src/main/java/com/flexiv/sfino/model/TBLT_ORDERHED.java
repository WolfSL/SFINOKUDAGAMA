package com.flexiv.sfino.model;

import java.io.Serializable;
import java.util.List;

public class TBLT_ORDERHED implements Serializable {
    private String DocNo;
    private String Discode;
    private String AreaCode;
    private String LocCode;
    private String SalesDate;
    private String CusCode;
    private String RepCode;
    private String RefNo;
    private double GrossAmt;
    private double NetAmt;
    private double DisPer;
    private double Discount;
    private double VatAmt;
    private String PayType;
    private String CreateUser;
    private String Status;
    private boolean ISUSED;

    private List<TBLT_ORDDTL> ItemList;

    public List<TBLT_ORDDTL> getItemList() {
        return ItemList;
    }

    public void setItemList(List<TBLT_ORDDTL> itemList) {
        ItemList = itemList;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public String getDiscode() {
        return Discode;
    }

    public void setDiscode(String discode) {
        Discode = discode;
    }

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public String getLocCode() {
        return LocCode;
    }

    public void setLocCode(String locCode) {
        LocCode = locCode;
    }

    public String getSalesDate() {
        return SalesDate;
    }

    public void setSalesDate(String salesDate) {
        SalesDate = salesDate;
    }

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }

    public String getRepCode() {
        return RepCode;
    }

    public void setRepCode(String repCode) {
        RepCode = repCode;
    }

    public String getRefNo() {
        return RefNo;
    }

    public void setRefNo(String refNo) {
        RefNo = refNo;
    }

    public double getGrossAmt() {
        return GrossAmt;
    }

    public void setGrossAmt(double grossAmt) {
        GrossAmt = grossAmt;
    }

    public double getNetAmt() {
        return NetAmt;
    }

    public void setNetAmt(double netAmt) {
        NetAmt = netAmt;
    }

    public double getDisPer() {
        return DisPer;
    }

    public void setDisPer(double disPer) {
        DisPer = disPer;
    }

    public double getDiscount() {
        return Discount;
    }

    public void setDiscount(double discount) {
        Discount = discount;
    }

    public double getVatAmt() {
        return VatAmt;
    }

    public void setVatAmt(double vatAmt) {
        VatAmt = vatAmt;
    }

    public String getPayType() {
        return PayType;
    }

    public void setPayType(String payType) {
        PayType = payType;
    }

    public String getCreateUser() {
        return CreateUser;
    }

    public void setCreateUser(String createUser) {
        CreateUser = createUser;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public boolean isISUSED() {
        return ISUSED;
    }

    public void setISUSED(boolean ISUSED) {
        this.ISUSED = ISUSED;
    }


    @Override
    public String toString() {
        return "TBLT_ORDERHED{" +
                "DocNo='" + DocNo + '\'' +
                ", Discode='" + Discode + '\'' +
                ", AreaCode='" + AreaCode + '\'' +
                ", LocCode='" + LocCode + '\'' +
                ", SalesDate='" + SalesDate + '\'' +
                ", CusCode='" + CusCode + '\'' +
                ", RepCode='" + RepCode + '\'' +
                ", RefNo='" + RefNo + '\'' +
                ", GrossAmt=" + GrossAmt +
                ", NetAmt=" + NetAmt +
                ", DisPer=" + DisPer +
                ", Discount=" + Discount +
                ", VatAmt=" + VatAmt +
                ", PayType='" + PayType + '\'' +
                ", CreateUser='" + CreateUser + '\'' +
                ", Status='" + Status + '\'' +
                ", ISUSED=" + ISUSED +
                ", ItemList=" + ItemList +
                '}';
    }
}
