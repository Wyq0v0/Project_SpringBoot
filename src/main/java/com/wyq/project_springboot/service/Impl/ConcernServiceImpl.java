package com.wyq.project_springboot.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.moment.MomentDTO;
import com.wyq.project_springboot.dto.user.UserDTO;
import com.wyq.project_springboot.entity.Concern;
import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.mapper.ConcernMapper;
import com.wyq.project_springboot.mapper.UserMapper;
import com.wyq.project_springboot.service.ConcernService;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ConcernServiceImpl implements ConcernService {
    @Autowired
    private ConcernMapper concernMapper;
    @Autowired
    private UserMapper userMapper;

    @Override
    public Result insertConcern(int concernUserId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        if (concernUserId == userId) {
            throw new RuntimeException();
        }

        concernMapper.insertConcern(userId, concernUserId);

        return Result.success();
    }

    @Override
    public Result deleteConcern(int concernUserId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        concernMapper.deleteConcern(userId, concernUserId);

        return Result.success();
    }

    @Override
    public Result updateNotesName(int concernUserId, String notesName) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        concernMapper.updateNotesName(userId, concernUserId, notesName);

        return Result.success();
    }

    @Override
    public Result getConcernList(int userId, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取客户端用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int viewerId = (Integer) userMap.get("id");

        //使用pageHelper进行倒叙、分页查询
        PageHelper.startPage(pageNum, pageSize, "record_time desc");
        Page<Concern> page = (Page<Concern>) concernMapper.selectConcernUserList(userId);

        //为concernUserListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为ListDTO的listData赋值
        List<UserDTO> UserDTOList = new ArrayList<>();
        for (Concern concern : page.getResult()) {
            UserDTO userDTO = new UserDTO();

            User user = userMapper.selectUserById(concern.getConcernId());
            userDTO.setUser(user);

            //判断查询关注列表的用户和被查询关注列表的用户是否相同，如果相同，则将userDTO中的isConcern设置为true，如果不同，则查询数据库
            if (userId == viewerId) {
                userDTO.setConcern(true);
                //设置备注名
                userDTO.setNotesName(concern.getNotesName());
            } else {
                Concern viewerConcern = concernMapper.selectConcern(viewerId, user.getId());

                //如果查询结果不为空则将关注状态设置为true并设置备注名
                if (viewerConcern != null) {
                    userDTO.setConcern(true);
                    userDTO.setNotesName(viewerConcern.getNotesName());
                } else {
                    userDTO.setConcern(false);
                }
            }

            UserDTOList.add(userDTO);
        }

        listDTO.setListData(UserDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result searchConcernUserList(String selectItem, String content, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取客户端用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        //该对象作为搜索条件
        Concern selectConcern = new Concern();
        selectConcern.setUserId(userId);

        //使用pageHelper进行倒叙、分页查询
        PageHelper.startPage(pageNum, pageSize, "record_time desc");
        Page<Concern> page = null;

        switch (selectItem) {
            case "notesName":
                selectConcern.setNotesName(content);
                page = (Page<Concern>) concernMapper.selectConcernListByCriteria(selectConcern);
                break;
            case "accountName":
                page = (Page<Concern>) concernMapper.selectConcernListByAccountName(userId, content);
                break;
            case "userId":
                Integer concernId = null;

                //判断content是否能够转为数字
                try {
                    concernId = Integer.parseInt(content);
                } catch (NumberFormatException e) {
                    return Result.error("用户ID不合法");
                }

                selectConcern.setConcernId(concernId);
                page = (Page<Concern>) concernMapper.selectConcernListByCriteria(selectConcern);
                break;
            default:
                throw new RuntimeException();
        }

        //为concernUserListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为ListDTO的listData赋值
        List<UserDTO> UserDTOList = new ArrayList<>();
        for (Concern concern : page.getResult()) {
            UserDTO userDTO = new UserDTO();

            User user = userMapper.selectUserById(concern.getConcernId());
            userDTO.setUser(user);

            //设置关注状态为true
            userDTO.setConcern(true);
            //设置备注名
            userDTO.setNotesName(concern.getNotesName());

            UserDTOList.add(userDTO);
        }

        listDTO.setListData(UserDTOList);

        return Result.success(listDTO);
    }

}
