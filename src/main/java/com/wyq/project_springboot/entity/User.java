package com.wyq.project_springboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private int id;
    private String accountName;
    private String phoneNumber;
    private String password;
    private int accountExp;//账号经验值
    private byte accountType;//账号类型
    private String profilePicturePath;//头像地址
    private String motto;//个性签名
    private UserInfo userInfo;//用户信息

    @JsonIgnore//用json传输user对象时忽略password
    public String getPassword() {
        return password;
    }

    //接收user对象时不忽略password
    public void setPassword(String password) {
        this.password = password;
    }
}
