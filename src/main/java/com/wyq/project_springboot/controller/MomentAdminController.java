package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.service.MomentAdminService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/admin/momentAdmin")
public class MomentAdminController {
    @Autowired
    private MomentAdminService momentAdminService;
    @GetMapping("/getMomentList")
    public Result getMomentList(@RequestParam String selectItem,String content,@RequestParam String sortBy, @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                @Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize) {
        return momentAdminService.getMomentList(selectItem,content,sortBy, sortOrder, pageNum, pageSize);
    }

    @DeleteMapping("/deleteMoment")
    public Result deleteMoment(@Min(1)Integer momentId){
        return momentAdminService.deleteMoment(momentId);
    }

    @GetMapping("/getCommentList")
    public Result getCommentList(@Min(1)Integer momentId,@Min(1)Integer pageNum, @Min(1) @Max(10)Integer pageSize){
        return momentAdminService.getCommentList(momentId,pageNum,pageSize);
    }

    @DeleteMapping("/deleteComment")
    public Result deleteComment(@Min(1)Integer commentId){
        return momentAdminService.deleteComment(commentId);
    }

    @PutMapping("/updateMoment")
    public Result updateMoment(@RequestBody Moment moment){
        return momentAdminService.updateMoment(moment);
    }


}
