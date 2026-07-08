import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import {
  AuctionListItem,
  normalizePageResponse,
  PageResponse,
  SpringPageResponse,
} from '../models/auction.model';

@Injectable({ providedIn: 'root' })
export class AuctionService {
  private readonly http = inject(HttpClient);
  private readonly endpoint = '/api/auctions';

  getFeaturedAuctions(): Observable<PageResponse<AuctionListItem>> {
    const params = new HttpParams()
      .append('status', 'ACTIVE')
      .append('status', 'SCHEDULED')
      .set('page', 0)
      .set('size', 6)
      .set('sort', 'endDate,asc');

    return this.http
      .get<SpringPageResponse<AuctionListItem>>(this.endpoint, { params })
      .pipe(map((response) => normalizePageResponse(response)));
  }
}
