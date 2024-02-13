package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.enumClass.NoticeType;

public interface NoticeAdminService {
    Result sendAdminNotice(String title, String content, NoticeType type, int receiverId);
}
