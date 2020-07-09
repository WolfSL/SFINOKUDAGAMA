package com.flexiv.sfino.model;

import java.math.BigDecimal;

public
class Bean_PromotionDetails extends Bean_PromotionMaster{
    private int RecLine;
    private String ItemCode;
    private BigDecimal QtyFrom;
    private BigDecimal QtyTo;
    private BigDecimal FQTY;
    private String PTCode;
    private int NoOfDealsQtyBalance;

    public int getNoOfDealsQtyBalance() {
        return NoOfDealsQtyBalance;
    }

    public void setNoOfDealsQtyBalance(int noOfDealsQtyBalance) {
        NoOfDealsQtyBalance = noOfDealsQtyBalance;
    }

    public int getRecLine() {
        return RecLine;
    }

    public void setRecLine(int recLine) {
        RecLine = recLine;
    }


    public String getItemCode() {
        return ItemCode;
    }

    public void setItemCode(String itemCode) {
        ItemCode = itemCode;
    }

    public BigDecimal getQtyFrom() {
        return QtyFrom;
    }

    public void setQtyFrom(BigDecimal qtyFrom) {
        QtyFrom = qtyFrom;
    }

    public BigDecimal getQtyTo() {
        return QtyTo;
    }

    public void setQtyTo(BigDecimal qtyTo) {
        QtyTo = qtyTo;
    }


    public BigDecimal getFQTY() {
        return FQTY;
    }

    public void setFQTY(BigDecimal FQTY) {
        this.FQTY = FQTY;
    }

    public String getPTCode() {
        return PTCode;
    }

    public void setPTCode(String PTCode) {
        this.PTCode = PTCode;
    }

    @Override
    public String toString() {
        return "Bean_PromotionDetails{" +
                "RecLine=" + RecLine +
                ", ItemCode='" + ItemCode + '\'' +
                ", QtyFrom=" + QtyFrom +
                ", QtyTo=" + QtyTo +
                ", FQTY=" + FQTY +
                ", PTCode='" + PTCode + '\'' +
                ", NoOfDealsQtyBalance=" + NoOfDealsQtyBalance +
                '}';
    }
}
