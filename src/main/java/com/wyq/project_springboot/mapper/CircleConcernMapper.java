package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.CircleConcern;
import org.apache.ibatis.annotations.Param;

public interface CircleConcernMapper {
    int insertConcern(@Param("userId")int userId, @Param("circleId")int circleId);
    int deleteConcern(@Param("userId")int userId, @Param("circleId")int circleId);
    CircleConcern selectCircleConcern(@Param("userId")int userId, @Param("circleId")int circleId);
}
