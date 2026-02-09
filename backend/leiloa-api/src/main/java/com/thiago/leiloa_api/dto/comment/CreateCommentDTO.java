package com.thiago.leiloa_api.dto.comment;

import java.util.UUID;

public record CreateCommentDTO(
    UUID auctionId,
    UUID parentCommentId,
    String content
) {}