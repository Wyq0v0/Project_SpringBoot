package com.wyq.project_springboot.dto.experience;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExpDTO {
    private int experience;
    private int nextLevelValue;
    private int level;
    private boolean isSignIn;

}
