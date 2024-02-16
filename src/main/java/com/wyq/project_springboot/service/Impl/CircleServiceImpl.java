package com.wyq.project_springboot.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.circle.CircleDTO;
import com.wyq.project_springboot.entity.*;
import com.wyq.project_springboot.entity.enumClass.CircleState;
import com.wyq.project_springboot.mapper.CircleConcernMapper;
import com.wyq.project_springboot.mapper.CircleMapper;
import com.wyq.project_springboot.mapper.UserMapper;
import com.wyq.project_springboot.service.CircleService;
import com.wyq.project_springboot.utils.ExpCalculateUtil;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.wyq.project_springboot.utils.ExpConstants.CREATE_CIRCLE_LEVEL;
import static com.wyq.project_springboot.utils.ImageUploadConstant.CIRCLE_PROFILE_PICTURE_PREFIX;
import static com.wyq.project_springboot.utils.ImageUploadConstant.IMAGE_UPLOAD_PREFIX;

@Service
@Transactional
@Slf4j
public class CircleServiceImpl implements CircleService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CircleMapper circleMapper;
    @Autowired
    private CircleConcernMapper circleConcernMapper;
    @Override
    public Result addCircle(String name, String motto, String detail, String rule, MultipartFile image) throws IOException {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        User user = userMapper.selectUserById(userId);
        Integer accountExp = user.getAccountExp();

        //判断用户经验等级是否足以创建圈子
        int LEVEL = ExpCalculateUtil.ExpToLevel(accountExp);
        if (LEVEL < CREATE_CIRCLE_LEVEL) {
            return Result.error("等级不足");
        }

        //保存图片
        //获取原始文件名
        String fileName = image.getOriginalFilename();
        //获取文件后缀
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        //使用UUID获取不重复的随机文件名
        fileName = UUID.randomUUID().toString() + fileSuffix;
        //将图片文件保存到文件目录下
        String fileRelativePath = CIRCLE_PROFILE_PICTURE_PREFIX + fileName;
        String finalAbsolutePath = IMAGE_UPLOAD_PREFIX + fileRelativePath;
        image.transferTo(new File(finalAbsolutePath));

        //创建circle对象
        Circle circle = new Circle(0, name, motto, detail, rule, fileRelativePath, userId, null, CircleState.UNAUDITED,0,0);
        circleMapper.insertCircle(circle);

        return Result.success("创建成功");
    }

    public Result searchCircleList(String searchValue,int pageNum,int pageSize){
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        ListDTO listDTO = new ListDTO();

        PageHelper.startPage(pageNum, pageSize);
        Page<Circle> page = (Page<Circle>) circleMapper.selectCircleByName(searchValue);

        listDTO.setTotal(page.getTotal());

        //为circleListDTO的circleDTOList赋值
        List<CircleDTO> circleDTOList = new ArrayList<>();
        for (Circle circle : page.getResult()) {
            //将circle转为circleDTO
            CircleDTO circleDTO = circleToCircleDTO(circle,userId);
            circleDTOList.add(circleDTO);
        }

        listDTO.setListData(circleDTOList);

        return Result.success(listDTO);
    }

    public Result getPopularCircleList(int pageNum,int pageSize){

        //TODO 搜索的圈子内容应该修改

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        ListDTO listDTO = new ListDTO();

        //使用pageHelper进行倒叙、分页查询
        PageHelper.startPage(pageNum, pageSize, "id desc");

        Page<Circle> page = (Page<Circle>) circleMapper.selectAllCircle();

        //为circleListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为circleListDTO的momentDTOList赋值
        List<CircleDTO> circleDTOList = new ArrayList<>();
        for (Circle circle : page.getResult()) {
            //将circle转为circleDTO
            CircleDTO circleDTO = circleToCircleDTO(circle,userId);
            circleDTOList.add(circleDTO);
        }
        listDTO.setListData(circleDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result getCircle(int circleId) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        Circle circle = circleMapper.selectCircle(circleId);
        CircleDTO circleDTO = circleToCircleDTO(circle, userId);
        return Result.success(circleDTO);
    }

    private CircleDTO circleToCircleDTO(Circle circle,int userId){
        CircleDTO circleDTO = new CircleDTO();
        circleDTO.setCircle(circle);

        User user = userMapper.selectUserById(circle.getCreatorId());

        //将手机号码去除
        user.setPassword(null);

        circleDTO.setCreator(user);

        //判断用户是否关注该圈子
        CircleConcern circleConcern = circleConcernMapper.selectCircleConcern(userId, circle.getId());
        if(circleConcern != null){
            circleDTO.setConcern(true);
        }else{
            circleDTO.setConcern(false);
        }

        return circleDTO;
    }
}