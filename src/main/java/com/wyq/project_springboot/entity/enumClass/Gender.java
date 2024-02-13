package com.wyq.project_springboot.entity.enumClass;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    UNKNOWN(0,"未知"), MAN(1,"男"), WOMAN(2,"女");
    private int value;
    private String gender;

    Gender(int value,String gender) {
        this.value = value;
        this.gender = gender;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @JsonValue
    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }
}
