package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Circle;
import com.wyq.project_springboot.entity.enumClass.CircleState;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CircleMapper {
    int insertCircle(Circle circle);
    List<Circle> selectCircleByName(@Param("name")String name);
    Circle selectCircle(@Param("circleId")int circleId);
    List<Circle> selectAllCircle();
    List<Circle> selectCircleListByCriteria(Circle circle);
    List<Circle> selectUnauditedCircle();
    int updateCircleState(@Param("circleId")int circleId,@Param("state")CircleState state);
}
