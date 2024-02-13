package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;

public interface ConcernService {

    Result insertConcern(int concernUserId);

    Result deleteConcern(int concernUserId);

    Result updateNotesName(int concernUserId,String notesName);

    Result getConcernList(int userId, int pageNum, int pageSize);

    Result searchConcernUserList(String selectItem, String content, int pageNum, int pageSize);
}
