import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { RoleRoutingModule } from './role-routing.module';
import { RoleComponent } from './role.component';
import { RoleIndexComponent } from './role-index/role-index.component';
import { FormsModule } from '@angular/forms';
import { RoleNewComponent } from './role-new/role-new.component';
import { RoleEditComponent } from './role-edit/role-edit.component';


@NgModule({
  declarations: [
    RoleComponent,
    RoleIndexComponent,
    RoleNewComponent,
    RoleEditComponent
  ],
  imports: [
    CommonModule,
    RoleRoutingModule,
    FormsModule
  ]
})
export class RoleModule { }
