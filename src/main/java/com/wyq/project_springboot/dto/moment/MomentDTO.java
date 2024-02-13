package com.wyq.project_springboot.dto.moment;

import com.wyq.project_springboot.entity.Circle;
import com.wyq.project_springboot.entity.Moment;
import com.wyq.project_springboot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomentDTO {
    private Moment moment;
    private User user;
    private boolean isThumbsUp;
    private Moment forwardMoment;
    private Circle circle;
}
