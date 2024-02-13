package com.wyq.project_springboot.entity.enumClass;

public enum ChatRead {
    UNREAD(0,"未读"), READ(1,"已读");
    private int value;
    private String isRead;

    ChatRead(int value, String isRead) {
        this.value = value;
        this.isRead = isRead;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }
}
