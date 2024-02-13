package com.wyq.project_springboot.dto.user;

import com.wyq.project_springboot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private User user;
    private boolean isConcern;
    private String notesName;
}
