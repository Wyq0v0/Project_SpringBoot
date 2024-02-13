package com.wyq.project_springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private int id;
    private String accountName;
    private String phoneNumber;
    private String password;
    private byte adminType;
}
