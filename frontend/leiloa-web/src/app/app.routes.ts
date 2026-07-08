import { Routes } from '@angular/router';
import { adminGuard, authGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    title: 'Leiloa | Boas histórias encontram novos donos',
    loadComponent: () =>
      import('./features/landing/landing-page').then((component) => component.LandingPage),
  },
  {
    path: 'leiloes',
    title: 'Leilões | Leiloa',
    loadComponent: () => import('./features/catalog/catalog-page').then((c) => c.CatalogPage),
  },
  {
    path: 'leiloes/:id',
    title: 'Detalhes do leilão | Leiloa',
    loadComponent: () =>
      import('./features/auction-detail/auction-detail-page').then((c) => c.AuctionDetailPage),
  },
  {
    path: 'entrar',
    canActivate: [guestGuard],
    title: 'Entrar | Leiloa',
    data: { mode: 'login' },
    loadComponent: () =>
      import('./features/auth/auth-page').then((component) => component.AuthPage),
  },
  {
    path: 'minha-area',
    canActivate: [authGuard],
    title: 'Minha área | Leiloa',
    loadComponent: () => import('./features/user-area/user-area-page').then((c) => c.UserAreaPage),
  },
  {
    path: 'meus-itens',
    canActivate: [authGuard],
    title: 'Meus itens | Leiloa',
    loadComponent: () => import('./features/items/items-page').then((c) => c.ItemsPage),
  },
  {
    path: 'enderecos',
    canActivate: [authGuard],
    title: 'Endereços | Leiloa',
    loadComponent: () => import('./features/addresses/addresses-page').then((c) => c.AddressesPage),
  },
  {
    path: 'pagamentos/:id',
    canActivate: [authGuard],
    title: 'Finalizar pagamento | Leiloa',
    loadComponent: () => import('./features/payment/payment-page').then((c) => c.PaymentPage),
  },
  {
    path: 'notificacoes',
    canActivate: [authGuard],
    title: 'Notificações | Leiloa',
    loadComponent: () =>
      import('./features/notifications/notifications-page').then((c) => c.NotificationsPage),
  },
  {
    path: 'conta',
    canActivate: [authGuard],
    title: 'Configurações da conta | Leiloa',
    loadComponent: () => import('./features/account/account-page').then((c) => c.AccountPage),
  },
  {
    path: 'admin',
    canActivate: [adminGuard],
    title: 'Administração | Leiloa',
    loadComponent: () => import('./features/admin/admin-page').then((c) => c.AdminPage),
  },
  {
    path: 'cadastro',
    canActivate: [guestGuard],
    title: 'Criar conta | Leiloa',
    data: { mode: 'register' },
    loadComponent: () =>
      import('./features/auth/auth-page').then((component) => component.AuthPage),
  },
  { path: '**', redirectTo: '' },
];
