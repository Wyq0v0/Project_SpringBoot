package com.wyq.project_springboot.entity.enumClass;

public enum ChatHide {
    UNHIDE(0,"未隐藏"), HIDE(1,"已隐藏");
    private int value;
    private String isHide;

    ChatHide(int value, String isHide) {
        this.value = value;
        this.isHide = isHide;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getIsHide() {
        return isHide;
    }

    public void setIsHide(String isHide) {
        this.isHide = isHide;
    }
}
