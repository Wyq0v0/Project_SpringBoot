package com.wyq.project_springboot.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.circle.CircleDTO;
import com.wyq.project_springboot.dto.moment.MomentDTO;
import com.wyq.project_springboot.entity.Circle;
import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.entity.enumClass.CircleState;
import com.wyq.project_springboot.mapper.CircleMapper;
import com.wyq.project_springboot.mapper.UserMapper;
import com.wyq.project_springboot.service.CircleAdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static com.wyq.project_springboot.entity.enumClass.CircleState.AUDITED;
import static com.wyq.project_springboot.entity.enumClass.CircleState.REJECTED;

@Service
@Transactional
@Slf4j
public class CircleAdminServiceImpl implements CircleAdminService {
    @Autowired
    private CircleMapper circleMapper;
    @Autowired
    private UserMapper userMapper;
    @Override
    public Result getCircleList(String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //该对象作为搜索条件
        Circle selectCircle = new Circle();

        PageHelper.startPage(pageNum, pageSize, sortBy + " " + sortOrder);
        Page<Circle> page = null;

        switch (selectItem) {
            case "circleId":
                selectCircle.setId(Integer.parseInt(content));
                page = (Page<Circle>) circleMapper.selectCircleListByCriteria(selectCircle);
                break;
            case "circleName":
                selectCircle.setName(content);
                page = (Page<Circle>) circleMapper.selectCircleListByCriteria(selectCircle);
                break;
            case "userId":
                selectCircle.setCreatorId(Integer.parseInt(content));
                page = (Page<Circle>) circleMapper.selectCircleListByCriteria(selectCircle);
                break;
            default:
                throw new RuntimeException();
        }
        //为listDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为listDTO的circleDTO赋值
        List<CircleDTO> circleDTOList = new ArrayList<>();
        for (Circle circle : page.getResult()) {
            CircleDTO circleDTO = new CircleDTO();
            circleDTO.setCircle(circle);

            User creator = userMapper.selectUserById(circle.getCreatorId());

            circleDTO.setCreator(creator);

            circleDTOList.add(circleDTO);
        }
        listDTO.setListData(circleDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result getUnauditedCircleList(int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        PageHelper.startPage(pageNum, pageSize, "record_time asc");
        Page<Circle> page = (Page<Circle>) circleMapper.selectUnauditedCircle();

        //为listDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为listDTO的circleDTO赋值
        List<CircleDTO> circleDTOList = new ArrayList<>();
        for (Circle circle : page.getResult()) {
            CircleDTO circleDTO = new CircleDTO();
            circleDTO.setCircle(circle);

            User creator = userMapper.selectUserById(circle.getCreatorId());

            circleDTO.setCreator(creator);

            circleDTOList.add(circleDTO);
        }
        listDTO.setListData(circleDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result rejectCircleApply(int circleId) {
        Circle circle = circleMapper.selectCircle(circleId);
        if(circle == null){
            return Result.error("该圈子不存在");
        }
        circleMapper.updateCircleState(circleId,REJECTED);
        return Result.success();
    }

    @Override
    public Result passCircleApply(int circleId) {
        Circle circle = circleMapper.selectCircle(circleId);
        if(circle == null){
            return Result.error("该圈子不存在");
        }
        circleMapper.updateCircleState(circleId,AUDITED);
        return Result.success();
    }
}
