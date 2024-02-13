package com.wyq.project_springboot.service.Impl;

import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.experience.ExpDTO;
import com.wyq.project_springboot.dto.experience.ExpTaskDTO;
import com.wyq.project_springboot.dto.user.UserDTO;
import com.wyq.project_springboot.entity.*;
import com.wyq.project_springboot.mapper.ConcernMapper;
import com.wyq.project_springboot.mapper.ExpTaskMapper;
import com.wyq.project_springboot.mapper.UserInfoMapper;
import com.wyq.project_springboot.mapper.UserMapper;
import com.wyq.project_springboot.utils.ExpCalculateUtil;
import com.wyq.project_springboot.service.UserService;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.wyq.project_springboot.utils.ExpConstants.SIGN_IN_EXP;
import static com.wyq.project_springboot.utils.ImageUploadConstant.IMAGE_UPLOAD_PREFIX;
import static com.wyq.project_springboot.utils.ImageUploadConstant.USER_PROFILE_PICTURE_PREFIX;
import static com.wyq.project_springboot.utils.RedisConstants.*;

@Service
@Transactional
public class UserServiceImpl implements UserService {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;
    @Autowired
    private ConcernMapper concernMapper;
    @Autowired
    private ExpTaskMapper expTaskMapper;
    @Override
    public Result getUser(int userId) {
        UserDTO userDTO = new UserDTO();
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer ownId = (Integer) userMap.get("id");

        User user = userMapper.selectUserById(userId);
        Concern concern = concernMapper.selectConcern(ownId, user.getId());

        userDTO.setUser(user);
        if (concern != null) {
            userDTO.setConcern(true);
            userDTO.setNotesName(concern.getNotesName());
        } else {
            userDTO.setConcern(false);
        }

        return Result.success(userDTO);
    }

    @Override
    public Result getUserInfo(int userId) {
        UserDTO userDTO = new UserDTO();

        User user = userMapper.selectUserById(userId);
        UserInfo userInfo = userInfoMapper.selectUserInfo(userId);
        user.setUserInfo(userInfo);
        userDTO.setUser(user);

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer ownId = (Integer) userMap.get("id");

        //如果查询的用户不是客户端自己，则查询是否已关注
        if (ownId != userId) {
            Concern concern = concernMapper.selectConcern(ownId, userId);
            if (concern != null) {
                userDTO.setConcern(true);
                userDTO.setNotesName(concern.getNotesName());
            } else {
                userDTO.setConcern(false);
            }

        }
        return Result.success(userDTO);
    }

    @Override
    public Result updateProfilePicture(MultipartFile image) throws IOException {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        //获取原始文件名
        String fileName = image.getOriginalFilename();
        //获取文件后缀
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        //使用UUID获取不重复的随机文件名
        fileName = UUID.randomUUID().toString() + fileSuffix;

        //将图片文件保存到文件目录下
        String fileRelativePath = USER_PROFILE_PICTURE_PREFIX + fileName;
        String finalAbsolutePath = IMAGE_UPLOAD_PREFIX + fileRelativePath;
        image.transferTo(new File(finalAbsolutePath));

        //更新头像
        userMapper.updateProfilePicture(userId, fileRelativePath);

        return Result.success();
    }

    @Override
    public Result updateUserInfo(User user) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        user.setId(userId);

        userMapper.updateUser(user);

        return Result.success();
    }

    @Override
    public User findUserById(int userId) {
        User user = userMapper.selectUserById(userId);
        return user;
    }

    @Override
    public Result updatePassword(String newPasssword) {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        userMapper.updatePassword(userId, newPasssword);

        return Result.success();
    }

    @Override
    public Result signIn() {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //以userIdyyyyMM的形式，用位图存储每个月用户签到情况
        String suffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_IN_KEY + userId + suffix;

        //获取今天是本月第几天，并写入Redis中的位图
        int dayOfMonth = now.getDayOfMonth();

        //判断用户是否今天签到过
        Boolean bit = stringRedisTemplate.opsForValue().getBit(key, dayOfMonth - 1);
        if (bit == true) {
            return Result.error("今日已签到过");
        }

        //将Redis中用户签到位图中本日设置为1
        stringRedisTemplate.opsForValue().setBit(key, dayOfMonth - 1, true);
        //增加用户经验
        userMapper.updateExperience(userId, SIGN_IN_EXP);
        //为Redis中用户任务完成情况该操作次数+1
        stringRedisTemplate.opsForZSet().add(EXP_TASK_KEY + userId,SIGN_IN_TASK_KEY,1);

        return Result.success("签到成功，经验+" + SIGN_IN_EXP);
    }

    @Override
    public Result getExpDetail() {
        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        User user = userMapper.selectUserById(userId);

        int accountExp = user.getAccountExp();
        int level = ExpCalculateUtil.ExpToLevel(accountExp);
        int nextLevelValue = ExpCalculateUtil.getNextLevelValue(accountExp);

        ExpDTO expDTO = new ExpDTO();
        expDTO.setExperience(accountExp);
        expDTO.setLevel(level);
        expDTO.setNextLevelValue(nextLevelValue);

        //获取当前时间
        LocalDateTime now = LocalDateTime.now();
        //以userIdyyyyMM的形式，用位图存储每个月用户签到情况
        String suffix = now.format(DateTimeFormatter.ofPattern(":yyyyMM"));
        String key = USER_SIGN_IN_KEY + userId + suffix;
        //获取今天是本月第几天，并写入Redis中的位图
        int dayOfMonth = now.getDayOfMonth();

        //判断并设置用户今天是否签到过
        expDTO.setSignIn(stringRedisTemplate.opsForValue().getBit(key, dayOfMonth - 1));

        return Result.success(expDTO);
    }

    @Override
    public Result getExpTaskList() {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        Integer userId = (Integer) userMap.get("id");

        //从数据库中查询所有任务
        List<ExpTask> expTasks = expTaskMapper.selectAllExpTask();

        List<ExpTaskDTO> expTaskDTOList = new ArrayList<>(expTasks.size());

        //遍历每个任务，并从Redis中查询该任务完成次数
        for (ExpTask expTask: expTasks) {
            ExpTaskDTO expTaskDTO = new ExpTaskDTO();
            expTaskDTO.setExpTask(expTask);
            Double finishedCount = stringRedisTemplate.opsForZSet().score(EXP_TASK_KEY + userId, Integer.toString(expTask.getId()));
            if(finishedCount != null){
                expTaskDTO.setFinishedCount(finishedCount.intValue());
            }else {
                expTaskDTO.setFinishedCount(0);
            }
            expTaskDTOList.add(expTaskDTO);
        }

        listDTO.setListData(expTaskDTOList);
        return Result.success(expTaskDTOList);
    }


}
