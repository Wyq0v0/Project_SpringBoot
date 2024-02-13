package com.wyq.project_springboot.entity.enumClass;

import com.fasterxml.jackson.annotation.JsonValue;

public enum CircleState {
    UNAUDITED(0,"未审核的"), AUDITED(1,"过审的"),REJECTED(2,"拒绝的");
    private int value;
    private String state;

    CircleState(int value, String state) {
        this.value = value;
        this.state = state;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @JsonValue
    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
