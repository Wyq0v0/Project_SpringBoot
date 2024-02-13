package com.wyq.project_springboot.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatDTO {
    private int type;
    private int senderId;
    private String content;
    private Date sendTime;
}
