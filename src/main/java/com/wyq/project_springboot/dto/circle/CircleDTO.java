package com.wyq.project_springboot.dto.circle;

import com.wyq.project_springboot.entity.Circle;
import com.wyq.project_springboot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CircleDTO {
    private Circle circle;
    private User creator;
    private boolean isConcern;
}
