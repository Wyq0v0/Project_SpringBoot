package com.wyq.project_springboot.entity;

import com.wyq.project_springboot.entity.enumClass.ChineseArea;
import com.wyq.project_springboot.entity.enumClass.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private int id;
    private int userId;
    private String realName;//真实姓名
    private Gender gender = Gender.UNKNOWN;
    private Date birthday;
    private ChineseArea location = ChineseArea.UNKNOWN;//所在地
    private Date registerTime;
}
