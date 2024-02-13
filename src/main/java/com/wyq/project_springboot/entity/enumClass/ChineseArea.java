package com.wyq.project_springboot.entity.enumClass;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ChineseArea {
    UNKNOWN(00,"未知"),
    BEIJING(11,"北京"),
    TIANJING(12,"天津"),
    HEBEI(13,"河北"),
    SHANXI(14,"山西"),
    INNER_MONGORIA(15,"内蒙古"),
    LIAONING(21,"辽宁"),
    JILIN(22,"吉林"),
    HEILONGJIANG(23,"黑龙江"),
    SHANGHAI(31,"上海"),
    JIANGSU(32,"江苏"),
    ZHEJIANG(33,"浙江"),
    ANHUI(34,"安徽"),
    FUJIAN(35,"福建"),
    JIANGXI(36,"江西"),
    SHANDONG(37,"山东"),
    HENAN(41,"河南"),
    HUBEI(42,"湖北"),
    HUNAN(43,"湖南"),
    GUANGDONG(44,"广东"),
    GUANGXI(45,"广西"),
    HAINAN(46,"海南"),
    CHONGQING(50,"重庆"),
    SICHUAN(51,"四川"),
    GUIZHOU(52,"贵州"),
    YUNNAN(53,"云南"),
    XIZANG(54,"西藏"),
    SHAANXI(61,"陕西"),
    GANSU(62,"甘肃"),
    QINGHAI(63,"青海"),
    NINGXIA(64,"宁夏"),
    XINJIANG(65,"新疆"),
    HONGKONG(81,"香港"),
    MACAO(82,"澳门"),
    TAIWAN(83,"台湾");
    private int value;
    private String area;

    ChineseArea(int value,String area) {
        this.value = value;
        this.area = area;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    @JsonValue
    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }
}
