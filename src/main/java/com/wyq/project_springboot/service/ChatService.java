package com.wyq.project_springboot.service;

import com.wyq.project_springboot.dto.chat.ChatMsgAndUserDTO;
import com.wyq.project_springboot.dto.chat.ChatListDTO;
import com.wyq.project_springboot.entity.Chat;
import com.wyq.project_springboot.entity.Result;

public interface ChatService {
    Result getChatUserList(int userId);
    Result getChatMsgList(int userId, int receiverId,String lastMark,int offset,int pageSize);
    Result getChatMsgAndUser(int userId, int receiverId,int pageSize);
    void insertChat(Chat chat);
    void insertChatRelation(int originatorId,int receiverId);

    Result getChatUser(int userId,int receiverId);
}
