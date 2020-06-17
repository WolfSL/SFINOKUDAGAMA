package com.flexiv.sfino.model;

public class TBLT_ORDDTL {

    private String ItemCode;
    private double ItQty;
    private double UnitPrice;
    private double DiscPer;
    private double DiscAmt;
    private double Amount;
    private String CusCode;
    private String Date;
    private int RecordLine;
    private double UsedQty;
    private double FQTY;
    private String BATCH;
    private String ItemName;

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public double getItQty() {
        return ItQty;
    }

    public void setItQty(double itQty) {
        ItQty = itQty;
    }

    public double getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        UnitPrice = unitPrice;
    }

    public double getDiscPer() {
        return DiscPer;
    }

    public void setDiscPer(double discPer) {
        DiscPer = discPer;
    }

    public double getDiscAmt() {
        return DiscAmt;
    }

    public void setDiscAmt(double discAmt) {
        DiscAmt = discAmt;
    }

    public double getAmount() {
        return Amount;
    }

    public void setAmount(double amount) {
        Amount = amount;
    }

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public int getRecordLine() {
        return RecordLine;
    }

    public void setRecordLine(int recordLine) {
        RecordLine = recordLine;
    }

    public double getUsedQty() {
        return UsedQty;
    }

    public void setUsedQty(double usedQty) {
        UsedQty = usedQty;
    }

    public double getFQTY() {
        return FQTY;
    }

    public void setFQTY(double FQTY) {
        this.FQTY = FQTY;
    }

    public String getBATCH() {
        return BATCH;
    }

    public void setBATCH(String BATCH) {
        this.BATCH = BATCH;
    }
}
