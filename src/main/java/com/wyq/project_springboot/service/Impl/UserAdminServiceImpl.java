package com.wyq.project_springboot.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.user.UserDTO;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.entity.UserInfo;
import com.wyq.project_springboot.mapper.UserInfoMapper;
import com.wyq.project_springboot.mapper.UserMapper;
import com.wyq.project_springboot.utils.ImageUploadConstant;
import com.wyq.project_springboot.service.UserAdminService;
import com.wyq.project_springboot.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.wyq.project_springboot.utils.ImageUploadConstant.IMAGE_UPLOAD_PREFIX;
import static com.wyq.project_springboot.utils.ImageUploadConstant.USER_PROFILE_PICTURE_PREFIX;

@Service
@Transactional
public class UserAdminServiceImpl implements UserAdminService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private UserInfoMapper userInfoMapper;

    @Override
    public Result getUserList(String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        List<UserDTO> userDTOList = new ArrayList<>();
        //该对象作为搜索条件
        User selectUser = new User();

        PageHelper.startPage(pageNum, pageSize, sortBy + " " + sortOrder);
        Page<User> page = null;

        switch (selectItem) {
            case "userId":
                int userId = Integer.parseInt(content);
                User user = userMapper.selectUserById(userId);
                if(user!=null){
                    //查询用户个人信息
                    UserInfo userInfo = userInfoMapper.selectUserInfo(user.getId());
                    user.setUserInfo(userInfo);

                    UserDTO userDTO = new UserDTO();
                    userDTO.setUser(user);

                    //为userListDTO设置条目总数
                    listDTO.setTotal(1);

                    userDTOList.add(userDTO);
                }

                break;
            case "accountName":
                page = (Page<User>) userMapper.selectUserListByAccountName(content);
                //为userListDTO设置条目总数
                listDTO.setTotal(page.getTotal());

                for (User user1 : page.getResult()) {
                    //查询用户信息
                    UserInfo userInfo1 = userInfoMapper.selectUserInfo(user1.getId());
                    user1.setUserInfo(userInfo1);

                    UserDTO userDTO1 = new UserDTO();
                    userDTO1.setUser(user1);

                    userDTOList.add(userDTO1);
                }
                break;
        }

        listDTO.setListData(userDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result updateProfilePicture(int userId,MultipartFile image) throws IOException {

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
        userMapper.updateProfilePicture(userId,fileRelativePath);

        return Result.success();
    }

    @Override
    public Result updateUserInfo(User user) {
        if(user.getPassword() != "" && user.getPassword() != null){
            user.setPassword(MD5Util.setPasswordToMD5(user.getPassword()));
            userMapper.updateUserWithPassword(user);
        }else{
            userMapper.updateUser(user);
        }

        return Result.success();
    }


}
