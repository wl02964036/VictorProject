import { TodoResolver } from 'src/app/@resolves/todo.resolver';
import { TodoContentComponent } from './todo-content/todo-content.component';
import { TodoListComponent } from './todo-list/todo-list.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { GroupResolver } from 'src/app/@resolves/group.resolver';

const routes: Routes = [
  { path: 'list', component: TodoListComponent, resolve: {dataList: GroupResolver} },
  { path: ':groupId',
    children: [
      { path: ":action", component: TodoContentComponent, resolve: {dataList: TodoResolver} },
      { path: '', redirectTo: 'All', pathMatch: 'full' } // 預設導向 All
    ] 
  },
  { path: '', redirectTo: 'list', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class TodoRoutingModule { }
