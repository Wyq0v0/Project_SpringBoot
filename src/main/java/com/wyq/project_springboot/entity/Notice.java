package com.wyq.project_springboot.entity;

import com.wyq.project_springboot.entity.enumClass.NoticeType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notice {
    private int id;
    private int adminId;
    private String title;
    private String content;
    private Date recordTime;
    private NoticeType type = NoticeType.ALL;
}
