package com.thiago.leiloa_api.specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.thiago.leiloa_api.domain.comment.Comment;
import com.thiago.leiloa_api.domain.comment.CommentStatus;

public class CommentSpecification {

    public static Specification<Comment> auctionId(UUID auctionId) {
        return (root, query, cb) ->
            auctionId == null ? null : cb.equal(root.get("auction").get("id"), auctionId);
    }

    public static Specification<Comment> userId(UUID userId) {
        return (root, query, cb) ->
            userId == null ? null : cb.equal(root.get("user").get("id"), userId);
    }

    public static Specification<Comment> statusIn(List<CommentStatus> statuses) {
        return (root, query, cb) ->
            statuses == null || statuses.isEmpty()
                ? null
                : root.get("status").in(statuses);
    }

    public static Specification<Comment> parentId(UUID parentId) {
        return (root, query, cb) ->
            parentId == null ? null : cb.equal(root.get("parent").get("id"), parentId);
    }

    public static Specification<Comment> textContains(String queryText) {
        return (root, query, cb) ->
            (queryText == null || queryText.isBlank())
                ? null
                : cb.like(cb.lower(root.get("content")), "%" + queryText.toLowerCase() + "%");
    }

    public static Specification<Comment> createdBetween(LocalDateTime start, LocalDateTime end) {
        return (root, query, cb) -> {

            if (start == null && end == null) {
                return null;
            }

            if (start != null && end != null) {
                return cb.between(root.get("createdAt"), start, end);
            }

            return start != null ?
                    cb.greaterThanOrEqualTo(root.get("createdAt"), start)
                    : cb.lessThanOrEqualTo(root.get("createdAt"), end);
        };
    }
}

