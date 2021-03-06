package com.flexiv.sfino.model;

public
class TBLT_SALINVDET {

    private String DocNo;
    private String Discode;
    private int DocType;
    private String ItemCode;
    private double ItQty;
    private double ULQty;
    private double UnitPrice;
    private double CostPrice;
    private double DiscPer;
    private double DiscAmt;
    private double Amount;
    private String Crep;
    private String CusCode;
    private String ExpiryDate;
    private String Date;
    private String LocCode;
    private String ExpDate;
    private String ItemName;
    private String TourID;
    private double FQTY;
    private boolean Is_Damage;
    private double IncreaseAmt;
    private double Volume;
    private int LineID;


    public double getCostPrice() {
        return CostPrice;
    }

    public void setCostPrice(double costPrice) {
        CostPrice = costPrice;
    }

    public double getVolume() {
        return Volume;
    }

    public void setVolume(double volume) {
        Volume = volume;
    }

    public double getULQty() {
        return ULQty;
    }

    public void setULQty(double ULQty) {
        this.ULQty = ULQty;
    }

    public String getTourID() {
        return TourID;
    }

    public void setTourID(String tourID) {
        TourID = tourID;
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

    public int getDocType() {
        return DocType;
    }

    public void setDocType(int docType) {
        DocType = docType;
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

    public String getCrep() {
        return Crep;
    }

    public void setCrep(String crep) {
        Crep = crep;
    }

    public String getCusCode() {
        return CusCode;
    }

    public void setCusCode(String cusCode) {
        CusCode = cusCode;
    }

    public String getExpiryDate() {
        return ExpiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        ExpiryDate = expiryDate;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getLocCode() {
        return LocCode;
    }

    public void setLocCode(String locCode) {
        LocCode = locCode;
    }

    public String getExpDate() {
        return ExpDate;
    }

    public void setExpDate(String expDate) {
        ExpDate = expDate;
    }

    public double getFQTY() {
        return FQTY;
    }

    public void setFQTY(double FQTY) {
        this.FQTY = FQTY;
    }

    public boolean isIs_Damage() {
        return Is_Damage;
    }

    public void setIs_Damage(boolean is_Damage) {
        Is_Damage = is_Damage;
    }

    public double getIncreaseAmt() {
        return IncreaseAmt;
    }

    public void setIncreaseAmt(double increaseAmt) {
        IncreaseAmt = increaseAmt;
    }

    public int getLineID() {
        return LineID;
    }

    public void setLineID(int lineID) {
        LineID = lineID;
    }

    public String getItemName() {
        return ItemName;
    }

    public void setItemName(String itemName) {
        ItemName = itemName;
    }

    @Override
    public String toString() {
        return "TBLT_SALINVDET{" +
                "DocNo='" + DocNo + '\'' +
                ", Discode='" + Discode + '\'' +
                ", DocType=" + DocType +
                ", ItemCode='" + ItemCode + '\'' +
                ", ItQty=" + ItQty +
                ", UnitPrice=" + UnitPrice +
                ", DiscPer=" + DiscPer +
                ", DiscAmt=" + DiscAmt +
                ", Amount=" + Amount +
                ", Crep='" + Crep + '\'' +
                ", CusCode='" + CusCode + '\'' +
                ", ExpiryDate='" + ExpiryDate + '\'' +
                ", Date='" + Date + '\'' +
                ", LocCode='" + LocCode + '\'' +
                ", ExpDate='" + ExpDate + '\'' +
                ", ItemName='" + ItemName + '\'' +
                ", FQTY=" + FQTY +
                ", Is_Damage=" + Is_Damage +
                ", IncreaseAmt=" + IncreaseAmt +
                ", LineID=" + LineID +
                '}';
    }
}
