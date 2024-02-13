package com.wyq.project_springboot.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wyq.project_springboot.entity.Comment;
import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.Result;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MomentService {

    Result insertMoment(String title,String content,int forwardMomentId, MultipartFile[] images,int circle) throws IOException;
    Result deleteMoment(int momentId);
    Result getMomentList(int userId,int pageNum,int pageSize);
    Result getPopularMomentList(String lastMark,int offset,int pageSize);
    Result getMoment(int momentId) throws JsonProcessingException;
    Result getRecommendMomentList(String lastMark,int offset,int pageSize);
    Result getConcernUserMomentList(String lastMark,int offset,int pageSize);
    Result addMomentThumbsUp(int momentId);
    Result subMomentThumbsUp(int momentId);
    Result getMomentThumbsUpState(int momentId);
    Result insertComment(Comment comment, MultipartFile[] images) throws IOException;
    Result deleteComment(int commentId);
    Result getCommentList(int momentId, int pageNum, int pageSize);
    Result addCommentThumbsUp(int commentId);
    Result subCommentThumbsUp(int commentId);
    Result getCommentThumbsUpState(int commentId);
    Result searchMomentList(String searchValue,int pageNum,int pageSize);
    Result getCircleMomentList(Integer circleId, Integer pageNum, Integer pageSize);
}
