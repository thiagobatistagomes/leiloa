import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  AuctionDetail,
  AuctionFilters,
  AuctionListItem,
  normalizePageResponse,
  PageResponse,
  SpringPageResponse,
} from '../models/auction.model';
import {
  Address,
  AddressRequest,
  AdminDevice,
  AdminUser,
  AuctionRequest,
  Bid,
  Category,
  Comment,
  Delivery,
  Item,
  ItemRequest,
  Notification,
  Payment,
} from '../models/marketplace.model';

@Injectable({ providedIn: 'root' })
export class MarketplaceService {
  private readonly http = inject(HttpClient);
  private readonly api = '/api';
  getAuctions(filters: AuctionFilters = {}): Observable<PageResponse<AuctionListItem>> {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', filters.size ?? 12)
      .set('sort', 'endDate,asc');
    filters.status?.forEach((status) => (params = params.append('status', status)));
    if (filters.search) params = params.set('search', filters.search);
    if (filters.categoryId) params = params.set('categoryId', filters.categoryId);
    return this.getPage<AuctionListItem>(`${this.api}/auctions`, params);
  }
  getAuction(id: string) {
    return this.http.get<AuctionDetail>(`${this.api}/auctions/${id}`);
  }
  createAuction(data: AuctionRequest) {
    return this.http.post(`${this.api}/auctions`, data);
  }
  cancelAuction(id: string) {
    return this.http.patch<void>(`${this.api}/auctions/${id}/cancel`, {});
  }
  getCategories() {
    return this.http.get<Category[]>(`${this.api}/categories`);
  }
  getPublicItems(page = 0) {
    return this.getPage<Item>(`${this.api}/items/public`, { page, size: 12 });
  }
  getMyItems() {
    return this.http.get<Item[]>(`${this.api}/items/me`);
  }
  createItem(data: ItemRequest) {
    return this.http.post<Item>(`${this.api}/items`, data);
  }
  updateItem(id: string, data: ItemRequest) {
    return this.http.put<Item>(`${this.api}/items/me/${id}`, data);
  }
  deleteItem(id: string) {
    return this.http.delete<void>(`${this.api}/items/me/${id}`);
  }
  placeBid(auctionId: string, value: number) {
    return this.http.post<Bid>(`${this.api}/bids/place-bid`, { auctionId, value });
  }
  getBids(auctionId: string) {
    return this.getPage<Bid>(`${this.api}/bids/public/${auctionId}`, { page: 0, size: 10 });
  }
  getMyBids(page = 0) {
    return this.getPage<Bid>(`${this.api}/bids/me`, { page, size: 20 });
  }
  getComments(auctionId: string) {
    return this.http.get<Comment[]>(`${this.api}/comments/auction/${auctionId}/tree`);
  }
  createComment(auctionId: string, content: string, parentCommentId: string | null = null) {
    return this.http.post<Comment>(`${this.api}/comments`, { auctionId, content, parentCommentId });
  }
  updateComment(id: string, content: string) {
    return this.http.put<Comment>(`${this.api}/comments/edit/${id}`, { content });
  }
  deleteComment(id: string) {
    return this.http.delete<Comment>(`${this.api}/comments/${id}`);
  }
  getAddresses() {
    return this.getPage<Address>(`${this.api}/addresses/me`, { page: 0, size: 50 });
  }
  createAddress(data: AddressRequest) {
    return this.http.post<Address>(`${this.api}/addresses`, data);
  }
  updateAddress(id: string, data: AddressRequest) {
    return this.http.put<Address>(`${this.api}/addresses/${id}`, data);
  }
  deleteAddress(id: string) {
    return this.http.delete<void>(`${this.api}/addresses/${id}`);
  }
  setDefaultAddress(id: string) {
    return this.http.patch<Address>(`${this.api}/addresses/${id}/default`, {});
  }
  getPayments(page = 0) {
    return this.getPage<Payment>(`${this.api}/payments/me`, { page, size: 20 });
  }
  getPendingPayments() {
    return this.http.get<Payment[]>(`${this.api}/payments/me/pending`);
  }
  setPaymentAddress(paymentId: string, addressId: string, referenceNote: string) {
    return this.http.post(`${this.api}/payments/${paymentId}/address`, {
      addressId,
      referenceNote,
    });
  }
  approvePayment(paymentId: string) {
    return this.http.post(`${this.api}/payments/${paymentId}/approve`, {});
  }
  getDelivery(paymentId: string) {
    return this.http.get<Delivery>(`${this.api}/api/deliveries/payment/${paymentId}/details`);
  }
  getNotifications(userId: string, page = 0) {
    return this.getPage<Notification>(`${this.api}/notifications/all`, { userId, page, size: 30 });
  }
  getUnreadNotifications(userId: string) {
    return this.getPage<Notification>(`${this.api}/notifications/unread`, {
      userId,
      page: 0,
      size: 20,
    });
  }
  readNotification(id: string, userId: string) {
    return this.http.patch<void>(
      `${this.api}/notifications/${id}/read`,
      {},
      { params: { userId } },
    );
  }
  readAllNotifications(userId: string) {
    return this.http.patch<void>(`${this.api}/notifications/read-all`, {}, { params: { userId } });
  }
  updateName(name: string) {
    return this.http.patch(`${this.api}/users/me`, { name });
  }
  updatePhone(phoneNumber: string) {
    return this.http.patch(`${this.api}/users/me/phone`, { phoneNumber });
  }
  updatePassword(currentPassword: string, newPassword: string) {
    return this.http.patch<void>(`${this.api}/auth/update-password`, {
      currentPassword,
      newPassword,
    });
  }
  deactivateAccount() {
    return this.http.patch(`${this.api}/users/me/deactivate`, {});
  }

  getAdminUsers(filters: { status?: string; role?: string; search?: string; page?: number } = {}) {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', 20)
      .set('sort', 'createdAt,desc');
    if (filters.status) params = params.set('status', filters.status);
    if (filters.role) params = params.set('role', filters.role);
    if (filters.search) params = params.set('search', filters.search);
    return this.getPage<AdminUser>(`${this.api}/users/admin/audit`, params);
  }
  blockUser(userId: string) {
    return this.http.patch<AdminUser>(`${this.api}/users/${userId}/block`, {});
  }
  activateUser(userId: string) {
    return this.http.patch<AdminUser>(`${this.api}/users/${userId}/activate`, {});
  }
  getAdminPayments(filters: { status?: string; page?: number } = {}) {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', 20)
      .set('sort', 'createdAt,desc');
    if (filters.status) params = params.set('status', filters.status);
    return this.getPage<Payment>(`${this.api}/payments/admin`, params);
  }
  cancelAdminPayment(paymentId: string) {
    return this.http.patch(`${this.api}/payments/${paymentId}/cancel`, {});
  }
  expirePendingPayments() {
    return this.http.post<number>(`${this.api}/payments/expire-pending`, {});
  }
  finishAuction(auctionId: string) {
    return this.http.patch<void>(`${this.api}/auctions/${auctionId}/finish`, {});
  }
  getAdminDeliveries(filters: { status?: string; page?: number } = {}) {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', 20)
      .set('sort', 'createdAt,desc');
    if (filters.status) params = params.set('status', filters.status);
    return this.getPage<Delivery>(`${this.api}/api/deliveries/search`, params);
  }
  updateDeliveryStatus(deliveryId: string, status: string) {
    return this.http.patch<Delivery>(`${this.api}/api/deliveries/${deliveryId}/status`, { status });
  }
  getAdminBids(page = 0) {
    return this.getPage<Bid>(`${this.api}/bids`, { page, size: 20 });
  }
  getAdminComments(filters: { status?: string; query?: string; page?: number } = {}) {
    let params = new HttpParams()
      .set('page', filters.page ?? 0)
      .set('size', 20)
      .set('sort', 'createdAt,desc');
    if (filters.status) params = params.append('status', filters.status);
    if (filters.query) params = params.set('query', filters.query);
    return this.getPage<Comment>(`${this.api}/comments/admin`, params);
  }
  blockComment(commentId: string) {
    return this.http.patch<Comment>(`${this.api}/comments/${commentId}/block`, {});
  }
  unblockComment(commentId: string) {
    return this.http.patch<Comment>(`${this.api}/comments/${commentId}/unblock`, {});
  }
  createCategory(name: string) {
    return this.http.post<Category>(`${this.api}/categories`, { name });
  }
  updateCategory(id: string, name: string) {
    return this.http.put<Category>(`${this.api}/categories/${id}`, { name });
  }
  deleteCategory(id: string) {
    return this.http.delete<void>(`${this.api}/categories/${id}`);
  }
  getAdminDevices(page = 0) {
    return this.getPage<AdminDevice>(`${this.api}/admin/devices/all`, {
      page,
      size: 20,
      sort: 'createdAt,desc',
    });
  }

  private getPage<T>(
    url: string,
    params: HttpParams | Record<string, string | number | boolean | readonly (string | number | boolean)[]>,
  ): Observable<PageResponse<T>> {
    return this.http
      .get<SpringPageResponse<T>>(url, { params })
      .pipe(map((response) => normalizePageResponse(response)));
  }
}
