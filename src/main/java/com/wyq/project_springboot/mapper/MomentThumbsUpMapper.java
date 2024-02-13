package com.wyq.project_springboot.mapper;

import org.apache.ibatis.annotations.Param;

public interface MomentThumbsUpMapper {

    int insertMomentThumbsUp(@Param("userId")int userId, @Param("momentId")int momentId);
    int deleteMomentThumbsUp(@Param("userId")int userId,@Param("momentId")int momentId);
    int selectMomentThumbsUp(@Param("userId")int userId,@Param("momentId")int momentId);

}
