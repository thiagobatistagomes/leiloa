-- 1. Adicionar coluna created_at com timestamp automático
ALTER TABLE payments
ADD COLUMN created_at TIMESTAMP DEFAULT now() NOT NULL;

-- 2. Adicionar coluna paid_at (pode ser nula)
ALTER TABLE payments
ADD COLUMN paid_at TIMESTAMP;

-- 3. Adicionar coluna expired_at (pode ser nula)
ALTER TABLE payments
ADD COLUMN expired_at TIMESTAMP;


