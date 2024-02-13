package com.wyq.project_springboot.dto.chat;

import com.wyq.project_springboot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMsgAndUserDTO {
    private int type;
    private ChatUserDTO chatUserDTO;
    private List chatList;
}
