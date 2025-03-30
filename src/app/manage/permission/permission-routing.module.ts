import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PermissionIndexComponent } from './permission-index/permission-index.component';
import { PermissionEditComponent } from './permission-edit/permission-edit.component';

const routes: Routes = [
  { path: 'index', component: PermissionIndexComponent },
  { path: 'edit', component: PermissionEditComponent },
  { path: "", redirectTo: "index", pathMatch: "full" }, // 根路徑跳到index
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PermissionRoutingModule { }
