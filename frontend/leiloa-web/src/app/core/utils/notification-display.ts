const notificationLabels: Record<string, string> = {
  USER_REGISTERED: 'Conta',
  AUCTION_PUBLISHED: 'Novo leilão',
  NEW_BID: 'Novo lance',
  OUT_BID: 'Lance superado',
  TIME_REMAINING: 'Leilão terminando',
  AUCTION_FINISHED: 'Leilão encerrado',
  PAYMENT_CREATED: 'Pagamento disponível',
  PAYMENT_APPROVED: 'Pagamento confirmado',
  PAYMENT_FAILED: 'Falha no pagamento',
  PAYMENT_CANCELLED: 'Pagamento cancelado',
  PAYMENT_EXPIRED: 'Pagamento expirado',
};

const notificationIcons: Record<string, string> = {
  USER_REGISTERED: '✓',
  AUCTION_PUBLISHED: '◇',
  NEW_BID: '↗',
  OUT_BID: '!',
  TIME_REMAINING: '◷',
  AUCTION_FINISHED: '◆',
  PAYMENT_CREATED: '$',
  PAYMENT_APPROVED: '✓',
  PAYMENT_FAILED: '!',
  PAYMENT_CANCELLED: '×',
  PAYMENT_EXPIRED: '◷',
};

export function notificationLabel(typeCode: string): string {
  return notificationLabels[typeCode] ?? 'Atualização';
}

export function notificationIcon(typeCode: string): string {
  return notificationIcons[typeCode] ?? '•';
}

export function notificationDate(value: string): string {
  const date = new Date(value);
  const now = new Date();
  const time = new Intl.DateTimeFormat('pt-BR', { hour: '2-digit', minute: '2-digit' }).format(
    date,
  );
  const startOfToday = new Date(now.getFullYear(), now.getMonth(), now.getDate()).getTime();
  const startOfDate = new Date(date.getFullYear(), date.getMonth(), date.getDate()).getTime();
  const dayDifference = Math.round((startOfToday - startOfDate) / 86_400_000);

  if (dayDifference === 0) return `Hoje, às ${time}`;
  if (dayDifference === 1) return `Ontem, às ${time}`;

  const formattedDate = new Intl.DateTimeFormat('pt-BR', {
    day: '2-digit',
    month: 'long',
    year: date.getFullYear() === now.getFullYear() ? undefined : 'numeric',
  }).format(date);
  return `${formattedDate}, às ${time}`;
}
