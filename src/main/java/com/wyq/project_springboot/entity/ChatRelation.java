package com.wyq.project_springboot.entity;

import com.wyq.project_springboot.entity.enumClass.ChatHide;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRelation {
    private int id;
    private int originatorId;
    private int receiverId;
    private ChatHide isHide = ChatHide.UNHIDE;
}
