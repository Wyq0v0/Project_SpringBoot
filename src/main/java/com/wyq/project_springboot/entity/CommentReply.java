package com.wyq.project_springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentReply {
    private int id;
    private int userId;
    private int commentId;
    private String content;
    private Date recordTime;
}
