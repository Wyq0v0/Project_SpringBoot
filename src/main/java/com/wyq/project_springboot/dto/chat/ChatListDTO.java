package com.wyq.project_springboot.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatListDTO {
    private int type;
    private String lastMark;
    private int offset;
    private List listData;
}
