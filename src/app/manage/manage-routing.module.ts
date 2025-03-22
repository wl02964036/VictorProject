import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { ManageComponent } from './manage.component';
import { HomeComponent } from './home/home.component';
import { TodoComponent } from './todo/todo.component';

const routes: Routes = [
      {
          path: "", component: ManageComponent, // 父層 -> Menu tree & header外框
          children: [
              { path: "home", component: HomeComponent }, // 子層 -> 首頁
              { path: "todo", component: TodoComponent }, // 子層 -> Todo
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
