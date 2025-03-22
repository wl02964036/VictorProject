import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { TodoComponent } from './todo/todo.component';
import { NotfoundComponent } from './notfound/notfound.component';

const routes: Routes = [
    { path: "home", component: HomeComponent }, // 指定路徑匹配Component
    { path: "todo", component: TodoComponent }, // 指定路徑匹配Component
    { path: "", redirectTo: "home", pathMatch: "full" }, // 只有Domain則跳到home
    { path: "**", component: NotfoundComponent } // 非上面任一個path則跳到notfound
];

@NgModule({
    imports: [RouterModule.forRoot(routes)],
    exports: [RouterModule]
})
export class AppRoutingModule { }