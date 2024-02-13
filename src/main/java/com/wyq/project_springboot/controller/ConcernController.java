package com.wyq.project_springboot.controller;

import com.wyq.project_springboot.entity.Result;
import com.wyq.project_springboot.service.ConcernService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@Validated
@RequestMapping("/concern")
public class ConcernController {

    @Autowired
    private ConcernService concernService;

    @PostMapping("/addConcern")
    public Result addConcern(@Min(10000)Integer concernUserId){
        return concernService.insertConcern(concernUserId);

    }

    @DeleteMapping("/deleteConcern")
    public Result deleteConcern(@Min(10000)Integer concernUserId){
        return concernService.deleteConcern(concernUserId);
    }

    @PatchMapping("/updateNotesName")
    public Result updateNotesName(@Min(10000)Integer concernUserId,@Pattern(regexp = "^\\S{0,20}$")String notesName){
        return concernService.updateNotesName(concernUserId,notesName);
    }

    @GetMapping("/getConcernUserList")
    public Result getConcernUserList(@Min(10000)Integer userId,@Min(1)Integer pageNum, @Min(1) @Max(10)Integer pageSize){
        return concernService.getConcernList(userId, pageNum, pageSize);
    }

    @GetMapping("/searchConcernUserList")
    public Result searchConcernUserList(@RequestParam String selectItem,String content, @Min(1) Integer pageNum, @Min(1) @Max(20) Integer pageSize){
        return concernService.searchConcernUserList(selectItem,content,pageNum,pageSize);
    }
}
