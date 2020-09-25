package com.flexiv.sfino.model;

import java.io.Serializable;
import java.util.List;

public class DefModalList implements Serializable {

    private List<DefModal> list;

    public List<DefModal> getList() {
        return list;
    }

    public void setList(List<DefModal> list) {
        this.list = list;
    }
}
