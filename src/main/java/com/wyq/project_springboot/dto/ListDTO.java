package com.wyq.project_springboot.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListDTO {
    private long total;
    private String lastMark;
    private int offset;
    private List listData;
}
