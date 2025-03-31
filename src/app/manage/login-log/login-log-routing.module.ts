import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginLogIndexComponent } from './login-log-index/login-log-index.component';

const routes: Routes = [
  { path: 'index', component: LoginLogIndexComponent },
  { path: "", redirectTo: "index", pathMatch: "full" }, // 根路徑跳到index
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class LoginLogRoutingModule { }
