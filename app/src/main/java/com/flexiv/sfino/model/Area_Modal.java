package com.flexiv.sfino.model;

import java.io.Serializable;

public class Area_Modal implements Serializable {
    private String AreaCode;
    private String AreaName;
    private String DisCode;

    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public String getAreaName() {
        return AreaName;
    }

    public void setAreaName(String areaName) {
        AreaName = areaName;
    }

    public String getDisCode() {
        return DisCode;
    }

    public void setDisCode(String disCode) {
        DisCode = disCode;
    }
}
