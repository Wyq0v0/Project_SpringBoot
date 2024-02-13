package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Moment;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MomentMapper {

    int insertMoment(Moment moment);
    int deleteMoment(@Param("userId") int userId, @Param("momentId") int momentId);
    int updateMoment(Moment moment);
    List<Moment> selectUserMomentList(@Param("userId") int userId);
    Moment selectMoment(@Param("momentId") int momentId);
    List<Moment> selectMomentByIdList(@Param("momentIdList")List momentIdList);
    List<Moment> selectConcernUserMomentList(@Param("userId") int userId);
    int addMomentThumbsUpCount(@Param("momentId")int momentId);
    int subMomentThumbsUpCount(@Param("momentId")int momentId);
    int addMomentCommentCount(@Param("momentId")int momentId);
    int subMomentCommentCount(@Param("momentId")int momentId);
    List<Moment> selectMomentListByCriteria(Moment moment);
    List<Moment> selectMomentListByAccountName(@Param("accountName")String accountName);

    List<Moment> selectAllMoment();

    List<Moment> selectCircleMomentList(@Param("circleId") int circleId);
}
