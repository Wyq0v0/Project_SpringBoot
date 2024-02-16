package com.wyq.project_springboot.entity.enumClass;

import com.fasterxml.jackson.annotation.JsonValue;

public enum DeleteState {
    UNDELETED(0,"未删除"), DELETED(1,"已删除");
    private int value;
    private String state;

    DeleteState(int value, String state) {
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
