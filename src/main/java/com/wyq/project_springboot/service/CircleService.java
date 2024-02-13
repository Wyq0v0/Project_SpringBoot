package com.wyq.project_springboot.service;

import com.wyq.project_springboot.entity.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface CircleService {

    Result addCircle(String name, String motto, String detail, String rule, MultipartFile image) throws IOException;
    Result searchCircleList(String searchValue,int pageNum,int pageSize);
    Result getPopularCircleList(int pageNum,int pageSize);

    Result getCircle(int circleId);
}
