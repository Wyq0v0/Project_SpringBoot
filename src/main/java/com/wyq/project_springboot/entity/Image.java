package com.wyq.project_springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Image {
    private int id;
    private int dependId;
    private String imagePath;
    private long imageSize;
    private Date recordTime;
}
