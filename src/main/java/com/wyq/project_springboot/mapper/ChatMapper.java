package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Chat;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

public interface ChatMapper {
    Chat selectLastChat(@Param("senderId")int senderId, @Param("receiverId")int receiverId);

    List<Chat> selectChatList(@Param("senderId")int senderId, @Param("receiverId")int receiverId, @Param("lastMark") Date lastMark, @Param("offset")int offset, @Param("pageSize")int pageSize);

    int insertChat(Chat chat);
}
