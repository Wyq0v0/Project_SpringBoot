package com.wyq.project_springboot.entity.enumClass;

import com.fasterxml.jackson.annotation.JsonValue;

public enum NoticeType {
    ALL(0,"全体"),PERSON(1,"个人");
    private int value;
    private String type;

    NoticeType(int value, String type) {
        this.value = value;
        this.type = type;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @JsonValue
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
