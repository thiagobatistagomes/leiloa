package com.thiago.leiloa_api.dto.comment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;





@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentTreeDTO {
    private UUID id;
    private UUID userId;
    private String userName;
    private String content;
    private LocalDateTime createdAt;
    private List<CommentTreeDTO> replies = new ArrayList<>();
}
