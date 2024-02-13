package com.wyq.project_springboot.dto.chat;

import com.wyq.project_springboot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatUserDTO {
    private int type;
    private User user;
    private String notesName;
    private Date lastMsgTime;
    private boolean isRead;

}
