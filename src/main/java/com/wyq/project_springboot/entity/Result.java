package com.wyq.project_springboot.entity;

import com.wyq.project_springboot.entity.enumClass.ResultCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private int code;
    private String msg;
    private T data;

    public static <E> Result<E> success(E data){
        return new Result<>(ResultCode.SUCCESS.getState(),"成功",data);
    }

    public static Result success(){
        return new Result(ResultCode.SUCCESS.getState(),"成功",null);
    }

    public static Result error(String msg){
        return new Result(ResultCode.ERROR.getState(),msg,null);
    }
}
