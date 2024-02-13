package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.service.NoticeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Validated
@RequestMapping("/notice")
public class NoticeController {
    @Autowired
    private NoticeService noticeService;
    @GetMapping("/getNoticeList")
    public Result getNoticeList(@Min(1) Integer pageNum, @Min(1) @Max(10) Integer pageSize){
        return noticeService.getNoticeList(pageNum,pageSize);
    }
}
