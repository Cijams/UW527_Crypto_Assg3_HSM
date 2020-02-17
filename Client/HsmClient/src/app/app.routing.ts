import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { HomeComponent } from './home';
import { LoginComponent } from './login';
import { RegisterComponent } from './register';

/**
 * User for navigating around the app.
 */
const routes: Routes = [
  {
    path: '', component: HomeComponent
  },
  {
    path: 'login', component: LoginComponent
  },
  {
    path: 'register', component: RegisterComponent
  },

  // Catch all for non-mapped routes.
  {
    path: '**', redirectTo: ''
  }
];

export const AppRoutingModule = RouterModule.forRoot(routes);