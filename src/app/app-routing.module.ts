import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './manage/home/home.component';
import { NotfoundComponent } from './notfound/notfound.component';
import { LoginComponent } from './login/login.component';
import { ManageComponent } from './manage/manage.component';
import { TodoComponent } from './manage/todo/todo.component';

// 指定路徑匹配Component
const routes: Routes = [
    { path: "login", loadChildren: () => import("./login/login.module").then(m => m.LoginModule) }, // 延遲載入 -> 登入畫面 
    {
        path: "manage", loadChildren: () => import("./manage/manage.module").then(m => m.ManageModule), // 父層 -> Menu tree & header外框
    },
    { path: "", redirectTo: "login", pathMatch: "full" }, // 根路徑跳到login
    { path: "**", component: NotfoundComponent } // 非上面任一個path則跳到notfound
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }