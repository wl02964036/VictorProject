import { TodoContentComponent } from './todo-content/todo-content.component';
import { TodoListComponent } from './todo-list/todo-list.component';
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

const routes: Routes = [
  { path: 'list', component: TodoListComponent },
  { path: ':groupId',
    children: [
      { path: ":action", component: TodoContentComponent },
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
