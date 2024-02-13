package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.service.CircleAdminService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/admin/circleAdmin")
public class CircleAdminController {
    @Autowired
    private CircleAdminService circleAdminService;

    @GetMapping("/getCircleList")
    public Result getCircleList(@RequestParam String selectItem, String content, @RequestParam String sortBy, @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                @Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize) {
        return circleAdminService.getCircleList(selectItem,content,sortBy, sortOrder, pageNum, pageSize);
    }

    @GetMapping("/getUnauditedCircleList")
    public Result getUnauditedCircleList(@Min(1)Integer pageNum, @Min(1) @Max(20)Integer pageSize){
        return circleAdminService.getUnauditedCircleList(pageNum,pageSize);
    }

    @PutMapping("/rejectCircleApply")
    public Result rejectCircleApply(Integer circleId){
        return circleAdminService.rejectCircleApply(circleId);
    }

    @PutMapping("/passCircleApply")
    public Result passCircleApply(Integer circleId){
        return circleAdminService.passCircleApply(circleId);
    }

}
