package com.wyq.project_springboot.service.Impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.wyq.project_springboot.dto.ListDTO;
import com.wyq.project_springboot.dto.comment.CommentDTO;
import com.wyq.project_springboot.dto.moment.MomentDTO;
import com.wyq.project_springboot.entity.Comment;
import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.User;
import com.wyq.project_springboot.mapper.*;
import com.wyq.project_springboot.service.MomentAdminService;
import com.wyq.project_springboot.utils.ThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
public class MomentAdminServiceImpl implements MomentAdminService {
    @Autowired
    private MomentMapper momentMapper;
    @Autowired
    private MomentImageMapper momentImageMapper;
    @Autowired
    private MomentThumbsUpMapper momentThumbsUpMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CommentMapper commentMapper;
    @Autowired
    private CommentImageMapper commentImageMapper;
    @Autowired
    private CommentThumbsUpMapper commentThumbsUpMapper;

    @Override
    public Result getMomentList(String selectItem, String content, String sortBy, String sortOrder, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //该对象作为搜索条件
        Moment selectMoment = new Moment();

        PageHelper.startPage(pageNum, pageSize, sortBy + " " + sortOrder);
        Page<Moment> page = null;

        switch (selectItem) {
            case "titleAndContent":
                selectMoment.setTitle(content);
                selectMoment.setContent(content);
                page = (Page<Moment>) momentMapper.selectMomentListByCriteria(selectMoment);
                break;
            case "title":
                selectMoment.setTitle(content);
                page = (Page<Moment>) momentMapper.selectMomentListByCriteria(selectMoment);
                break;
            case "content":
                selectMoment.setContent(content);
                page = (Page<Moment>) momentMapper.selectMomentListByCriteria(selectMoment);
                break;
            case "userId":
                selectMoment.setUserId(Integer.parseInt(content));
                page = (Page<Moment>) momentMapper.selectMomentListByCriteria(selectMoment);
                break;
            case "accountName":
                page = (Page<Moment>) momentMapper.selectMomentListByAccountName(content);
                break;
            default:
                throw new RuntimeException();
        }
        //为momentListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为momentListDTO的momentDTOList赋值
        List<MomentDTO> momentDTOList = new ArrayList<>();
        for (Moment moment : page.getResult()) {
            MomentDTO momentDTO = new MomentDTO();
            momentDTO.setMoment(moment);

            User user = userMapper.selectUserById(moment.getUserId());

            momentDTO.setUser(user);

            momentDTOList.add(momentDTO);
        }
        listDTO.setListData(momentDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result deleteMoment(int momentId) {
        //获取动态的userId
        Moment moment = momentMapper.selectMoment(momentId);

        //删除动态
        momentMapper.deleteMoment(moment.getUserId(), momentId);
        momentImageMapper.deleteMomentImage(momentId);

        return Result.success();
    }

    @Override
    public Result getCommentList(int momentId, int pageNum, int pageSize) {
        ListDTO listDTO = new ListDTO();

        //获取用户ID
        Map<String, Object> userMap = ThreadLocalUtil.get();
        int userId = (Integer) userMap.get("id");

        //使用pageHelper进行分页查询
        PageHelper.startPage(pageNum, pageSize);
        Page<Comment> page = (Page<Comment>) commentMapper.selectCommentList(momentId);

        //为commentListDTO设置条目总数
        listDTO.setTotal(page.getTotal());

        //为commentListDTO的commentDTOList赋值
        List<CommentDTO> commentDTOList = new ArrayList<>();
        for (Comment comment : page.getResult()) {
            CommentDTO commentDTO = new CommentDTO();
            commentDTO.setComment(comment);

            User user = userMapper.selectUserById(comment.getUserId());

            //将手机号码去除
            user.setPassword(null);
            commentDTO.setUser(user);

            //查看用户是否点赞过该评论
            int isThumbsUp = commentThumbsUpMapper.selectCommentThumbsUp(userId, comment.getId());
            if (isThumbsUp > 0) {
                commentDTO.setThumbsUp(true);
            } else {
                commentDTO.setThumbsUp(false);
            }

            commentDTOList.add(commentDTO);
        }
        listDTO.setListData(commentDTOList);

        return Result.success(listDTO);
    }

    @Override
    public Result deleteComment(int commentId) {
        Comment comment = commentMapper.selectComment(commentId);
        //删除评论
        commentMapper.deleteComment(comment.getUserId(), commentId);
        commentImageMapper.deleteCommentImage(commentId);
        //动态评论数-1
        momentMapper.subMomentCommentCount(comment.getMomentId());

        return Result.success();
    }

    @Override
    public Result updateMoment(Moment moment) {
        momentMapper.updateMoment(moment);
        return Result.success();
    }


}
