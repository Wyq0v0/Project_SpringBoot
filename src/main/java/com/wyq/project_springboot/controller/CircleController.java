package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.service.CircleService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@Validated
@RequestMapping("/circle")
public class CircleController {
    @Autowired
    private CircleService circleService;

    @PostMapping("/addCircle")
    public Result addCircle(String name, String motto, String detail, String rule, MultipartFile image) throws IOException {
        return circleService.addCircle(name, motto, detail, rule, image);
    }

    @GetMapping("/searchCircleList")
    public Result searchCircleList(String searchValue, @Min(1) Integer pageNum, @Min(1) @Max(20) Integer pageSize){
        return circleService.searchCircleList(searchValue,pageNum,pageSize);
    }

    @GetMapping("/getPopularCircleList")
    public Result getPopularCircleList(@Min(1) Integer pageNum, @Min(1) @Max(10) Integer pageSize){
        return circleService.getPopularCircleList(pageNum,pageSize);
    }

    @GetMapping("/getCircle")
    public Result getCircle(@Min(1)Integer circleId){
        return circleService.getCircle(circleId);
    }
}
