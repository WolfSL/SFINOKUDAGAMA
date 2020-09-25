package com.flexiv.sfino.model;

import java.io.Serializable;
import java.util.List;

public
class TBLT_SALINVHED implements Serializable {

    private String DocNo;
    private String Discode;
    private Integer DocType;
    private String SalesDate;
    private String SupCode;
    private String CusCode;
    private String RepCode;
    private String RefNo;
    private Integer InvType;
    private String LocCode;
    private String CrDrType;
    private String DueDate;
    private double DueAmount;
    private double GrossAmt;
    private double AddTax1;
    private double AddTax2;
    private double AddTax3;
    private double DedAmt1;
    private double DedAmt2;
    private double DedAmt3;
    private double NetAmt;
    private double RefDueAmt;
    private double Discount;
    private double VatAmt;
    private String OrdRefNo;
    private String PayType;
    private String CreateUser;
    private boolean GLTransfer;
    private String Status;
    private double DisPer;
    private int trType;
    private String AreaCode;
    private boolean ISPRINT;
    private double DamageQty;
    private double IncreaseAmt;
    private double DamageDue;
    private String SalesRep;
    private String varID;
    private String TourID;

    public String getTourID() {
        return TourID;
    }

    public void setTourID(String tourID) {
        TourID = tourID;
    }

    private List<TBLT_SALINVDET> ItemList;

    public List<TBLT_SALINVDET> getItemList() {
        return ItemList;
    }

    public void setItemList(List<TBLT_SALINVDET> itemList) {
        ItemList = itemList;
    }

    public String getVarID() {
        return varID;
    }

    public void setVarID(String varID) {
        this.varID = varID;
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

    public Integer getDocType() {
        return DocType;
    }

    public void setDocType(Integer docType) {
        DocType = docType;
    }

    public String getSalesDate() {
        return SalesDate;
    }

    public void setSalesDate(String salesDate) {
        SalesDate = salesDate;
    }

    public String getSupCode() {
        return SupCode;
    }

    public void setSupCode(String supCode) {
        SupCode = supCode;
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

    public Integer getInvType() {
        return InvType;
    }

    public void setInvType(Integer invType) {
        InvType = invType;
    }

    public String getLocCode() {
        return LocCode;
    }

    public void setLocCode(String locCode) {
        LocCode = locCode;
    }

    public String getCrDrType() {
        return CrDrType;
    }

    public void setCrDrType(String crDrType) {
        CrDrType = crDrType;
    }

    public String getDueDate() {
        return DueDate;
    }

    public void setDueDate(String dueDate) {
        DueDate = dueDate;
    }

    public double getDueAmount() {
        return DueAmount;
    }

    public void setDueAmount(double dueAmount) {
        DueAmount = dueAmount;
    }

    public double getGrossAmt() {
        return GrossAmt;
    }

    public void setGrossAmt(double grossAmt) {
        GrossAmt = grossAmt;
    }

    public double getAddTax1() {
        return AddTax1;
    }

    public void setAddTax1(double addTax1) {
        AddTax1 = addTax1;
    }

    public double getAddTax2() {
        return AddTax2;
    }

    public void setAddTax2(double addTax2) {
        AddTax2 = addTax2;
    }

    public double getAddTax3() {
        return AddTax3;
    }

    public void setAddTax3(double addTax3) {
        AddTax3 = addTax3;
    }

    public double getDedAmt1() {
        return DedAmt1;
    }

    public void setDedAmt1(double dedAmt1) {
        DedAmt1 = dedAmt1;
    }

    public double getDedAmt2() {
        return DedAmt2;
    }

    public void setDedAmt2(double dedAmt2) {
        DedAmt2 = dedAmt2;
    }

    public double getDedAmt3() {
        return DedAmt3;
    }

    public void setDedAmt3(double dedAmt3) {
        DedAmt3 = dedAmt3;
    }

    public double getNetAmt() {
        return NetAmt;
    }

    public void setNetAmt(double netAmt) {
        NetAmt = netAmt;
    }

    public double getRefDueAmt() {
        return RefDueAmt;
    }

    public void setRefDueAmt(double refDueAmt) {
        RefDueAmt = refDueAmt;
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

    public String getOrdRefNo() {
        return OrdRefNo;
    }

    public void setOrdRefNo(String ordRefNo) {
        OrdRefNo = ordRefNo;
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

    public boolean isGLTransfer() {
        return GLTransfer;
    }

    public void setGLTransfer(boolean GLTransfer) {
        this.GLTransfer = GLTransfer;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public double getDisPer() {
        return DisPer;
    }

    public void setDisPer(double disPer) {
        DisPer = disPer;
    }

    public int getTrType() {
        return trType;
    }

    public void setTrType(int trType) {
        this.trType = trType;
    }

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public boolean isISPRINT() {
        return ISPRINT;
    }

    public void setISPRINT(boolean ISPRINT) {
        this.ISPRINT = ISPRINT;
    }

    public double getDamageQty() {
        return DamageQty;
    }


    public void setDamageQty(double damageQty) {
        DamageQty = damageQty;
    }

    public double getIncreaseAmt() {
        return IncreaseAmt;
    }

    public void setIncreaseAmt(double increaseAmt) {
        IncreaseAmt = increaseAmt;
    }

    public double getDamageDue() {
        return DamageDue;
    }

    public void setDamageDue(double damageDue) {
        DamageDue = damageDue;
    }

    public String getSalesRep() {
        return SalesRep;
    }

    public void setSalesRep(String salesRep) {
        SalesRep = salesRep;
    }


    @Override
    public String toString() {
        return "TBLT_SALINVHED{" +
                "DocNo='" + DocNo + '\'' +
                ", Discode='" + Discode + '\'' +
                ", DocType=" + DocType +
                ", SalesDate='" + SalesDate + '\'' +
                ", SupCode='" + SupCode + '\'' +
                ", CusCode='" + CusCode + '\'' +
                ", RepCode='" + RepCode + '\'' +
                ", RefNo='" + RefNo + '\'' +
                ", InvType=" + InvType +
                ", LocCode='" + LocCode + '\'' +
                ", CrDrType='" + CrDrType + '\'' +
                ", DueDate='" + DueDate + '\'' +
                ", DueAmount=" + DueAmount +
                ", GrossAmt=" + GrossAmt +
                ", AddTax1=" + AddTax1 +
                ", AddTax2=" + AddTax2 +
                ", AddTax3=" + AddTax3 +
                ", DedAmt1=" + DedAmt1 +
                ", DedAmt2=" + DedAmt2 +
                ", DedAmt3=" + DedAmt3 +
                ", NetAmt=" + NetAmt +
                ", RefDueAmt=" + RefDueAmt +
                ", Discount=" + Discount +
                ", VatAmt=" + VatAmt +
                ", OrdRefNo='" + OrdRefNo + '\'' +
                ", PayType='" + PayType + '\'' +
                ", CreateUser='" + CreateUser + '\'' +
                ", GLTransfer=" + GLTransfer +
                ", Status='" + Status + '\'' +
                ", DisPer=" + DisPer +
                ", trType=" + trType +
                ", AreaCode='" + AreaCode + '\'' +
                ", ISPRINT=" + ISPRINT +
                ", DamageQty=" + DamageQty +
                ", IncreaseAmt=" + IncreaseAmt +
                ", DamageDue=" + DamageDue +
                ", SalesRep='" + SalesRep + '\'' +
                '}';
    }
}
