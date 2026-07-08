export interface Category {
  id: string;
  name: string;
}
export type ItemStatus = 'ACTIVE' | 'IN_AUCTION' | 'SOLD' | 'DELETED';
export interface Item {
  id: string;
  name: string;
  description: string;
  imageUrl: string | null;
  categoryId: string;
  categoryName: string;
  sellerId: string;
  sellerName: string;
  status: ItemStatus;
  createdAt: string;
}
export interface ItemRequest {
  name: string;
  description: string;
  imageUrl: string | null;
  categoryId: string;
}
export interface AuctionRequest {
  itemId: string;
  startPrice: number;
  minIncrement: number;
  startDate: string;
  endDate: string;
}
export interface Bid {
  id?: string;
  value: number;
  createdAt: string;
  auctionId?: string;
  auctionName?: string;
  itemName?: string;
  bidderId?: string;
  bidderName?: string;
  bidder?: string;
}
export type CommentStatus = 'VISIBLE' | 'HIDDEN' | 'BLOCKED' | 'DELETED';
export interface Comment {
  id: string;
  auctionId?: string;
  userId: string;
  userName: string;
  parentCommentId?: string | null;
  content: string;
  status?: CommentStatus;
  createdAt: string;
  updatedAt?: string;
  replies?: Comment[];
}
export interface Address {
  id: string;
  userId: string;
  label: string;
  street: string;
  number: string;
  complement: string | null;
  district: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  isDefault: boolean;
  createdAt: string;
  updatedAt: string;
}
export type AddressRequest = Omit<Address, 'id' | 'userId' | 'createdAt' | 'updatedAt'>;
export type PaymentStatus = 'PENDING' | 'COMPLETED' | 'EXPIRED' | 'CANCELLED';
export interface PaymentAddress {
  id: string;
  paymentId: string;
  street: string;
  number: string;
  complement: string | null;
  district: string;
  city: string;
  state: string;
  country: string;
  postalCode: string;
  referenceNote: string | null;
  createdAt: string;
}
export interface Payment {
  id: string;
  auctionId: string;
  auctionName?: string;
  itemName?: string;
  winnerId?: string;
  value: number;
  status: PaymentStatus;
  createdAt: string;
  paidAt: string | null;
  expiredAt: string | null;
  address?: PaymentAddress | null;
}
export interface Delivery {
  id: string;
  paymentId: string;
  status: string;
  deliveryMethod: string;
  trackingCode: string | null;
  shippedAt: string | null;
  deliveredAt: string | null;
  createdAt: string;
  updatedAt: string;
  address: PaymentAddress;
}
export interface Notification {
  id: string;
  typeCode: string;
  title: string;
  message: string;
  data: Record<string, unknown> | null;
  createdAt: string;
  readAt: string | null;
}

export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'BLOCKED';
export interface AdminUser {
  id: string;
  name: string;
  email: string;
  status: UserStatus;
  createdAt: string;
  updatedAt: string | null;
  lastLoginAt: string | null;
  phoneNumber: string | null;
  roles: string[];
}
export type DeliveryStatus =
  | 'PENDING'
  | 'PROCESSING'
  | 'SHIPPED'
  | 'IN_TRANSIT'
  | 'OUT_FOR_DELIVERY'
  | 'DELIVERED'
  | 'RETURN_REQUESTED'
  | 'RETURNED';
export interface AdminDevice {
  id: string;
  userId: string;
  userEmail: string;
  deviceType: string;
  deviceToken: string;
  userAgent: string | null;
  createdAt: string;
  lastUsedAt: string | null;
}
