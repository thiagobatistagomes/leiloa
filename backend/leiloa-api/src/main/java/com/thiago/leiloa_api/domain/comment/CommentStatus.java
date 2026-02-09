package com.thiago.leiloa_api.domain.comment;



public enum CommentStatus {
    VISIBLE,
    HIDDEN,   // ocultado pelo próprio usuário, reversível
    DELETED,  // exclusão lógica definitiva pelo usuário
    BLOCKED   // censurado/admin-only
}
