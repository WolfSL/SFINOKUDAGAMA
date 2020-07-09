package com.flexiv.sfino.model;

public
class Bean_PromotionMaster {
    private int PromoNo;
    private String PromoCode;
    private int PromoType;
    private String PromoDesc;
    private String DateFrom;
    private String DateTo;
    private int Active;
    private String ActiveDays;
    private String TargetCategory;
    private String Discode;

    public int getPromoNo() {
        return PromoNo;
    }

    public void setPromoNo(int promoNo) {
        PromoNo = promoNo;
    }

    public String getPromoCode() {
        return PromoCode;
    }

    public void setPromoCode(String promoCode) {
        PromoCode = promoCode;
    }

    public int getPromoType() {
        return PromoType;
    }

    public void setPromoType(int promoType) {
        PromoType = promoType;
    }

    public String getPromoDesc() {
        return PromoDesc;
    }

    public void setPromoDesc(String promoDesc) {
        PromoDesc = promoDesc;
    }

    public String getDateFrom() {
        return DateFrom;
    }

    public void setDateFrom(String dateFrom) {
        DateFrom = dateFrom;
    }

    public String getDateTo() {
        return DateTo;
    }

    public void setDateTo(String dateTo) {
        DateTo = dateTo;
    }

    public int getActive() {
        return Active;
    }

    public void setActive(int active) {
        Active = active;
    }

    public String getActiveDays() {
        return ActiveDays;
    }

    public void setActiveDays(String activeDays) {
        ActiveDays = activeDays;
    }

    public String getTargetCategory() {
        return TargetCategory;
    }

    public void setTargetCategory(String targetCategory) {
        TargetCategory = targetCategory;
    }

    public String getDiscode() {
        return Discode;
    }

    public void setDiscode(String discode) {
        Discode = discode;
    }
}
