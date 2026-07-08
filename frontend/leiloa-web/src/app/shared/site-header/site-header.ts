import { Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserActions } from '../user-actions/user-actions';

@Component({
  selector: 'app-site-header',
  imports: [RouterLink, UserActions],
  templateUrl: './site-header.html',
  styleUrl: './site-header.scss',
})
export class SiteHeader {
  readonly auth = inject(AuthService);
}
