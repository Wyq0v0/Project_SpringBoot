package com.wyq.project_springboot.mapper;

import com.wyq.project_springboot.entity.ExpTask;

import java.util.List;

public interface ExpTaskMapper {
    List<ExpTask> selectAllExpTask();
}
