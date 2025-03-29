import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { OrganizationIndexComponent } from './organization-index/organization-index.component';

const routes: Routes = [
  { path: 'index', component: OrganizationIndexComponent },
  { path: "", redirectTo: "index", pathMatch: "full" }, // 根路徑跳到index
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class OrganizationRoutingModule { }
