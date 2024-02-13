package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.Result;

public interface MomentAdminService {
    Result getMomentList(String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize);
    Result deleteMoment(int momentId);
    Result getCommentList(int momentId, int pageNum, int pageSize);
    Result deleteComment(int commentId);
    Result updateMoment(Moment moment);
}
