import { Component, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserActions } from '../user-actions/user-actions';

@Component({
  selector: 'app-header',
  imports: [RouterLink, RouterLinkActive, UserActions],
  templateUrl: './app-header.html',
  styleUrl: './app-header.scss',
})
export class AppHeader {
  readonly auth = inject(AuthService);
}
