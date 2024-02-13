package com.wyq.project_springboot.dto.comment;

import com.wyq.project_springboot.entity.Comment;
import com.wyq.project_springboot.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Comment comment;
    private User user;
    private boolean isThumbsUp;
}
