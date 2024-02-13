package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.ChatRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ChatRelationMapper {
    List<ChatRelation> selectChatRelationList(@Param("userId")int userId);

    int insertChatRelationList(ChatRelation chatRelation);
}
