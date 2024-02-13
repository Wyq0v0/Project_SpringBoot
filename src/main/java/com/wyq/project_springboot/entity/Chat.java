package com.wyq.project_springboot.entity;

import com.wyq.project_springboot.entity.enumClass.ChatRead;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    private Date recordTime;
    private ChatRead isRead = ChatRead.UNREAD;
}
