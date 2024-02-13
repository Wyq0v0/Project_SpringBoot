package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.entity.enumClass.NoticeType;
import com.wyq.project_springboot.service.NoticeAdminService;
import com.wyq.project_springboot.service.NoticeService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/admin/noticeAdmin")
public class NoticeAdminController {
    @Autowired
    private NoticeAdminService noticeAdminService;
    @Autowired
    private NoticeService noticeService;

    @PostMapping("/sendAdminNotice")
    public Result sendNotice(@RequestParam String title, @RequestParam String content, @RequestParam NoticeType type, Integer receiverId) {
        return noticeAdminService.sendAdminNotice(title, content, type, receiverId);
    }

    @GetMapping("/getNoticeList")
    public Result getNoticeList(@RequestParam String selectItem, String content, @RequestParam String sortBy, @RequestParam(required = false, defaultValue = "asc") String sortOrder,
                                @Min(1) Integer pageNum, @Min(1) @Max(20) Integer pageSize) {
        return null;
    }
}
