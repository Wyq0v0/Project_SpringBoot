package com.wyq.project_springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Moment {
    private int id;
    private int userId;
    private String title;
    private String content;
    private int quoteId;
    private int forwardCount;
    private int thumbsUpCount;
    private int commentCount;
    private Date recordTime;
    private int readCount;
    private int circleId;
    private int forwardMomentId;
    private List<Image> images;
}
