package com.wyq.project_springboot.dto.notice;

import com.wyq.project_springboot.entity.Notice;
import com.wyq.project_springboot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class noticeDTO {
    private Notice notice;
    private User sender;
    private boolean isRead;
}
