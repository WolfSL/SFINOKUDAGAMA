package com.flexiv.sfino.model;

import java.io.Serializable;
import java.util.ArrayList;

public class TBLT_CUSRECEIPTHED implements Serializable {
    String ReceiptNo,Discode,TrnDate,CusCode,RefNo,ChequeNo,BankCode,BankDate,RecType,CancelDate,Status,ChqDeposit,CardType,CardNo,RecDocType;
    int PayType,IsInvoice;
    double PaidAmt,NetAmt,CancelAmt;
    ArrayList<TBLT_CUSRECEIPTDTL> dtl;

    public ArrayList<TBLT_CUSRECEIPTDTL> getDtl() {
        return dtl;
    }

    public void setDtl(ArrayList<TBLT_CUSRECEIPTDTL> dtl) {
        this.dtl = dtl;
    }

    public String getReceiptNo() {
        return ReceiptNo;
    }

    public void setReceiptNo(String receiptNo) {
        ReceiptNo = receiptNo;
    }

    public String getDiscode() {
        return Discode;
    }

    public void setDiscode(String discode) {
        Discode = discode;
    }

    public String getTrnDate() {
        return TrnDate;
    }

    public void setTrnDate(String trnDate) {
        TrnDate = trnDate;
    }

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }

    public String getRefNo() {
        return RefNo;
    }

    public void setRefNo(String refNo) {
        RefNo = refNo;
    }

    public String getChequeNo() {
        return ChequeNo;
    }

    public void setChequeNo(String chequeNo) {
        ChequeNo = chequeNo;
    }

    public String getBankCode() {
        return BankCode;
    }

    public void setBankCode(String bankCode) {
        BankCode = bankCode;
    }

    public String getBankDate() {
        return BankDate;
    }

    public void setBankDate(String bankDate) {
        BankDate = bankDate;
    }

    public String getRecType() {
        return RecType;
    }

    public void setRecType(String recType) {
        RecType = recType;
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

    public String getChqDeposit() {
        return ChqDeposit;
    }

    public void setChqDeposit(String chqDeposit) {
        ChqDeposit = chqDeposit;
    }

    public String getCardType() {
        return CardType;
    }

    public void setCardType(String cardType) {
        CardType = cardType;
    }

    public String getCardNo() {
        return CardNo;
    }

    public void setCardNo(String cardNo) {
        CardNo = cardNo;
    }

    public int getPayType() {
        return PayType;
    }

    public void setPayType(int payType) {
        PayType = payType;
    }

    public int getIsInvoice() {
        return IsInvoice;
    }

    public void setIsInvoice(int isInvoice) {
        IsInvoice = isInvoice;
    }

    public String getRecDocType() {
        return RecDocType;
    }

    public void setRecDocType(String recDocType) {
        RecDocType = recDocType;
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
