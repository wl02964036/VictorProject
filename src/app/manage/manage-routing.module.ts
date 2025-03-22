import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageComponent } from './manage.component';

const routes: Routes = [
      {
          path: "", component: ManageComponent, // 父層 -> Menu tree & header外框
          children: [
              { path: 'home', loadChildren: () => import('./home/home.module').then(m => m.HomeModule) }, // 子層 -> 首頁
              { path: 'todo', loadChildren: () => import('./todo/todo.module').then(m => m.TodoModule) }, // 子層 -> Todo
              { path: 'account', loadChildren: () => import('./account/account.module').then(m => m.AccountModule) }, // 子層 -> 帳號管理
              { path: "", redirectTo: "home", pathMatch: "full" }, // 根路徑跳到home
          ]
      },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManageRoutingModule { }
