-- V2__seed_roles.sql
-- Seed das roles básicas do sistema

INSERT INTO roles (id, name, description)
VALUES
    (gen_random_uuid(), 'ROLE_USER', 'Usuário padrão'),
    (gen_random_uuid(), 'ROLE_ADMIN', 'Administrador do sistema')
ON CONFLICT (name) DO NOTHING;
