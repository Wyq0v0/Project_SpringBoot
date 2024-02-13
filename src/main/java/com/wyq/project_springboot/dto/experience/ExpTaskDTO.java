package com.wyq.project_springboot.dto.experience;

import com.wyq.project_springboot.entity.ExpTask;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExpTaskDTO {
    private ExpTask expTask;
    private int finishedCount;
}
