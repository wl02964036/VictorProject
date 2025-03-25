import { RoleResolver } from './../../@resolves/role.resolver';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { AccountComponent } from './account.component';

const routes: Routes = [
  {
    path: ':code', component: AccountComponent, resolve: {roleData: RoleResolver},
  },
  { path: '', redirectTo: 'ROLE_ADMIN', pathMatch: 'full' } // 預設導向 ROLE_ADMIN
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class AccountRoutingModule { }
