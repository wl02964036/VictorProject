import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageComponent } from './manage.component';

const routes: Routes = [
      {
          path: "", component: ManageComponent, // 父層 -> Menu tree & header外框
          children: [
              { path: 'index', loadChildren: () => import('./index/index.module').then(m => m.IndexModule) }, // 子層 -> 首頁
              { path: 'todo', loadChildren: () => import('./todo/todo.module').then(m => m.TodoModule) }, // 子層 -> Todo
              { path: 'account', loadChildren: () => import('./account/account.module').then(m => m.AccountModule) }, // 子層 -> 帳號管理
              { path: "", redirectTo: "index", pathMatch: "full" }, // 根路徑跳到index
          ]
      },
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class ManageRoutingModule { }
