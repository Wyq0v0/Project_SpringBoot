package com.wyq.project_springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Concern {
    private int id;
    private int userId;
    private int concernId;
    private String notesName;
    private Date recordTime;
}
