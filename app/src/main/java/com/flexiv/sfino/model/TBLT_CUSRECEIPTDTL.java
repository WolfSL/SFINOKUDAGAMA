package com.flexiv.sfino.model;

import java.io.Serializable;

public class TBLT_CUSRECEIPTDTL implements Serializable {
    public String Discode,RefNo,TrnDate,CancelDate,Status,InvDate,SupCode,RepCode;
    public double DueAmt,PaidAmt,NetAmt,CancelAmt;

    public String getDiscode() {
        return Discode;
    }

    public void setDiscode(String discode) {
        Discode = discode;
    }

    public String getRefNo() {
        return RefNo;
    }

    public void setRefNo(String refNo) {
        RefNo = refNo;
    }

    public String getTrnDate() {
        return TrnDate;
    }

    public void setTrnDate(String trnDate) {
        TrnDate = trnDate;
    }

    public String getCancelDate() {
        return CancelDate;
    }

    public void setCancelDate(String cancelDate) {
        CancelDate = cancelDate;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getInvDate() {
        return InvDate;
    }

    public void setInvDate(String invDate) {
        InvDate = invDate;
    }

    public String getSupCode() {
        return SupCode;
    }

    public void setSupCode(String supCode) {
        SupCode = supCode;
    }

    public String getRepCode() {
        return RepCode;
    }

    public void setRepCode(String repCode) {
        RepCode = repCode;
    }

    public double getDueAmt() {
        return DueAmt;
    }

    public void setDueAmt(double dueAmt) {
        DueAmt = dueAmt;
    }

    public double getPaidAmt() {
        return PaidAmt;
    }

    public void setPaidAmt(double paidAmt) {
        PaidAmt = paidAmt;
    }

    public double getNetAmt() {
        return NetAmt;
    }

    public void setNetAmt(double netAmt) {
        NetAmt = netAmt;
    }

    public double getCancelAmt() {
        return CancelAmt;
    }

    public void setCancelAmt(double cancelAmt) {
        CancelAmt = cancelAmt;
    }
}
