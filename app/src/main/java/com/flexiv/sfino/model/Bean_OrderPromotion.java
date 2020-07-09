package com.flexiv.sfino.model;

import com.flexiv.sfino.utill.SharedPreference;

import java.math.BigDecimal;

public class Bean_OrderPromotion extends Bean_PromotionDetails {

    private String DealCode;
    private int NoOfDeals;
    private BigDecimal Qty;
    private String DocNo;
    private int DocType;
    private String UserID = SharedPreference.COM_REP.getRepCode();
    private String RepCode = SharedPreference.COM_REP.getRepCode();
    private String CusCode;
    private BigDecimal FreeItem;
    private BigDecimal SysFQty;

    public BigDecimal getSysFQty() {
        return SysFQty;
    }

    public void setSysFQty(BigDecimal sysFQty) {
        SysFQty = sysFQty;
    }

    public String getDealCode() {
        return DealCode;
    }

    public void setDealCode(String dealCode) {
        DealCode = dealCode;
    }

    public int getNoOfDeals() {
        return NoOfDeals;
    }

    public void setNoOfDeals(int noOfDeals) {
        NoOfDeals = noOfDeals;
    }

    public BigDecimal getQty() {
        return Qty;
    }

    public void setQty(BigDecimal qty) {
        Qty = qty;
    }

    public String getDocNo() {
        return DocNo;
    }

    public void setDocNo(String docNo) {
        DocNo = docNo;
    }

    public int getDocType() {
        return DocType;
    }

    public void setDocType(int docType) {
        DocType = docType;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }


    public String getRepCode() {
        return RepCode;
    }

    public void setRepCode(String repCode) {
        RepCode = repCode;
    }

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }

    public BigDecimal getFreeItem() {
        return FreeItem;
    }

    public void setFreeItem(BigDecimal freeItem) {
        FreeItem = freeItem;
    }
}
