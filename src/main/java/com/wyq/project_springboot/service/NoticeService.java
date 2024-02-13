package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;

public interface NoticeService {
    Result sendSystemNotice();

    Result getNoticeList(int pageNum, int pageSize);
}
