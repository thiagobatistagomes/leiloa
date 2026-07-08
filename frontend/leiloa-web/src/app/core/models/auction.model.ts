export type AuctionStatus = 'ACTIVE' | 'SCHEDULED' | 'SOLD' | 'CANCELLED';

export interface AuctionListItem {
  auctionId: string;
  itemId: string;
  itemName: string;
  imageUrl: string | null;
  currentPrice: number;
  startDate: string;
  endDate: string;
  status: AuctionStatus;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface SpringPageResponse<T> {
  content?: T[];
  totalElements?: number;
  totalPages?: number;
  number?: number;
  size?: number;
  page?: {
    totalElements?: number;
    totalPages?: number;
    number?: number;
    size?: number;
  };
}

export function normalizePageResponse<T>(response: SpringPageResponse<T>): PageResponse<T> {
  const content = response.content ?? [];
  const page = response.page;

  return {
    content,
    totalElements: response.totalElements ?? page?.totalElements ?? content.length,
    totalPages: response.totalPages ?? page?.totalPages ?? 0,
    number: response.number ?? page?.number ?? 0,
    size: response.size ?? page?.size ?? content.length,
  };
}

export interface AuctionDetail extends AuctionListItem {
  startPrice: number;
  minIncrement: number;
  itemDescription: string;
  sellerId: string;
  sellerName: string;
}

export interface AuctionFilters {
  search?: string;
  status?: AuctionStatus[];
  categoryId?: string;
  page?: number;
  size?: number;
}
