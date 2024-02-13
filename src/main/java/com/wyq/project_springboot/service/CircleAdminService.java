package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;

public interface CircleAdminService {
    Result getCircleList(String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize);

    Result getUnauditedCircleList(int pageNum, int pageSize);

    Result rejectCircleApply(int circleId);

    Result passCircleApply(int circleId);
}
