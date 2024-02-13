package com.wyq.project_springboot.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.wyq.project_springboot.entity.Comment;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.service.MomentService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Validated
@RequestMapping("/moment")
public class MomentController {

    @Autowired
    private MomentService momentService;

    @PostMapping("/addMoment")
    public Result addMoment(String title, @Pattern(regexp = "^[\\s\\S]{1,2000}$") String content,Integer forwardMomentId, MultipartFile[] images,Integer circleId) throws IOException {
        return momentService.insertMoment(title,content,forwardMomentId, images,circleId);
    }

    @DeleteMapping("/deleteMoment")
    public Result deleteMoment(@Min(1) Integer momentId) {
        return momentService.deleteMoment(momentId);
    }

    @GetMapping("/getMomentList")
    public Result getMomentList(@Min(1) Integer userId, @Min(1) Integer pageNum, @Min(1) @Max(10) Integer pageSize) {
        return momentService.getMomentList(userId, pageNum, pageSize);
    }

    @GetMapping("/getPopularMomentList")
    public Result getPopularMomentList(String lastMark,Integer offset, @Min(1) @Max(10) Integer pageSize) {
        return momentService.getPopularMomentList(lastMark,offset, pageSize);
    }

    @GetMapping("/getMoment")
    public Result getMoment(@Min(1) Integer momentId) throws JsonProcessingException {
        return momentService.getMoment(momentId);
    }

    @GetMapping("/getRecommendMomentList")
    public Result getRecommendMomentList(String lastMark,Integer offset, @Min(1) @Max(10) Integer pageSize) {
        //根据点赞量搜索
        return momentService.getRecommendMomentList(lastMark,offset, pageSize);
    }

    @GetMapping("/getConcernUserMomentList")
    public Result getConcernUserMomentList(String lastMark,Integer offset, @Min(1) @Max(10) Integer pageSize) {
        return momentService.getConcernUserMomentList(lastMark,offset, pageSize);
    }

    @GetMapping("/getCircleMomentList")
    public Result getCircleMomentList(@Min(1) Integer circleId,@Min(1) Integer pageNum, @Min(1) @Max(10) Integer pageSize) {
        return momentService.getCircleMomentList(circleId,pageNum, pageSize);
    }

    @PostMapping("/addMomentThumbsUp")
    public Result addMomentThumbsUp(@Min(1) Integer momentId) throws Exception {
        return momentService.addMomentThumbsUp(momentId);
    }

    @DeleteMapping("/subMomentThumbsUp")
    public Result subMomentThumbsUp(@Min(1) Integer momentId) throws Exception {
        return momentService.subMomentThumbsUp(momentId);
    }

    @GetMapping("/getMomentThumbsUpState")
    public Result getMomentThumbsUpState(@Min(1) Integer momentId){
        return momentService.getMomentThumbsUpState(momentId);
    }

    @PostMapping("/addComment")
    public Result addComment(@Min(1) Integer momentId, @Pattern(regexp = "^[\\s\\S]{1,200}$") String content, MultipartFile[] images) throws IOException {
        Comment comment = new Comment();
        comment.setMomentId(momentId);
        comment.setContent(content);

        //将从客户端接收到的images传给service层处理
        return momentService.insertComment(comment, images);
    }

    @DeleteMapping("/deleteComment")
    public Result deleteComment(@Min(1) Integer commentId) {
        return momentService.deleteComment(commentId);
    }

    @GetMapping("/getCommentList")
    public Result getCommentList(@Min(1) Integer momentId, @Min(1) Integer pageNum, @Min(1) @Max(10) Integer pageSize) {
        return momentService.getCommentList(momentId, pageNum, pageSize);
    }

    @PostMapping("/addCommentThumbsUp")
    public Result addCommentThumbsUp(@Min(1) Integer commentId) throws Exception {
        return momentService.addCommentThumbsUp(commentId);
    }

    @DeleteMapping("/subCommentThumbsUp")
    public Result subCommentThumbsUp(@Min(1) Integer commentId) throws Exception {
        return momentService.subCommentThumbsUp(commentId);
    }

    @GetMapping("/getCommentThumbsUpState")
    public Result getCommentThumbsUpState(@Min(1) Integer commentId){
        return momentService.getCommentThumbsUpState(commentId);
    }

    @PostMapping("/replyComment")
    public Result replyComment() {
        return null;
    }

    @GetMapping("/searchMomentList")
    public Result searchMomentList(String searchValue, @Min(1) Integer pageNum, @Min(1) @Max(20) Integer pageSize) {
        return momentService.searchMomentList(searchValue, pageNum, pageSize);
    }

}
