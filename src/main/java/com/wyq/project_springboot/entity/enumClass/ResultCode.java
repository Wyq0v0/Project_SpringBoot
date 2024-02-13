package com.wyq.project_springboot.entity.enumClass;

/**
 * 返回信息的状态码枚举类
 */
public enum ResultCode {
    SUCCESS(0),
    ERROR(1);
    private int state;

    ResultCode(int state) {
        this.state = state;
    }

    public int getState() {
        return state;
    }
}
