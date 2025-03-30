import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageComponent } from './manage.component';

const routes: Routes = [
  {
    path: "", component: ManageComponent, // 父層 -> Menu tree & header外框
    children: [
      { path: 'index', loadChildren: () => import('./index/index.module').then(m => m.IndexModule) }, // 子層 -> 首頁
      { path: 'organization', loadChildren: () => import('./organization/organization.module').then(m => m.OrganizationModule) }, // 子層 -> 人員組織,
      { path: 'role', loadChildren: () => import('./role/role.module').then(m => m.RoleModule) }, // 子層 -> 角色管理,
      { path: "", redirectTo: "index", pathMatch: "full" }, // 根路徑跳到index
    ]
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManageRoutingModule { }
