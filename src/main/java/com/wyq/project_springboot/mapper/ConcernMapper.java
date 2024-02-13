package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.Concern;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.entity.UserInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ConcernMapper {

    int insertConcern(@Param("userId")int userId, @Param("concernUserId")int concernUserId);
    int deleteConcern(@Param("userId")int userId, @Param("concernUserId")int concernUserId);
    int updateNotesName(@Param("userId")int userId,@Param("concernUserId")int concernUserId,@Param("notesName")String notesName);
    List<Concern> selectConcernUserList(@Param("userId")int userId);
    List<Concern> selectBeConcernedList(@Param("userId")int userId);
    Concern selectConcern(@Param("userId")int userId, @Param("concernUserId")int concernUserId);
    List<Concern> selectConcernListByCriteria(Concern selectConcern);
    List<Concern> selectConcernListByAccountName(@Param("userId")int userId,@Param("accountName")String accountName);
}
