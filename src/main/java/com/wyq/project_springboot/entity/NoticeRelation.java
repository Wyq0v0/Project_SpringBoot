package com.wyq.project_springboot.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoticeRelation {
    private int id;
    private int noticeId;
    private int receiverId;
}
