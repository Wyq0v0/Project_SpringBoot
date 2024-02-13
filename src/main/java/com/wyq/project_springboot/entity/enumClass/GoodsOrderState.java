package com.wyq.project_springboot.entity.enumClass;

import com.fasterxml.jackson.annotation.JsonValue;

public enum GoodsOrderState {
    UNPAID(0,"未支付"), PAID(1,"已支付"),COMPLETED(2,"已完成"),RETURN(3,"退货中");
    private int value;
    private String state;

    GoodsOrderState(int value, String state) {
        this.value = value;
        this.state = state;
    }

    @JsonValue
    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
