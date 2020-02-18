import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home';
import { LoginComponent } from './login';
import { RegisterComponent } from './register';
import { HsmLoginComponent } from './components/hsm-login/hsm-login.component';
import { CryptoComponent } from './crypto/crypto.component';

/**
 * User for navigating around the app.
 */
const routes: Routes = [
  {
    path: '', component: HomeComponent
  },
  {
    path: 'crypto', component: CryptoComponent
  },
  {
    path: 'register', component: RegisterComponent
  },
  {
    path: 'login', component: HsmLoginComponent
  },

  // Catch all for non-mapped routes.
  {
    path: '**', redirectTo: ''
  }
];

export const AppRoutingModule = RouterModule.forRoot(routes);