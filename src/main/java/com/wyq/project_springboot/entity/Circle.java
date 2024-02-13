package com.wyq.project_springboot.entity;

import com.wyq.project_springboot.entity.enumClass.CircleState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Circle {
    private int id;
    private String name;
    private String motto;
    private String detail;
    private String rule;
    private String profilePicturePath;
    private int creatorId;
    private Date recordTime;
    private CircleState state = CircleState.UNAUDITED;
    private int concernCount;
    private int momentCount;
}
