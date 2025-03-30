import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RoleIndexComponent } from './role-index/role-index.component';
import { RoleNewComponent } from './role-new/role-new.component';
import { RoleEditComponent } from './role-edit/role-edit.component';

const routes: Routes = [
  { path: 'index', component: RoleIndexComponent },
  { path: 'new', component: RoleNewComponent },
  { path: 'edit', component: RoleEditComponent },
  { path: "", redirectTo: "index", pathMatch: "full" }, // 根路徑跳到index
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class RoleRoutingModule { }
