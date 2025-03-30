import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PermissionRoutingModule } from './permission-routing.module';
import { PermissionComponent } from './permission.component';
import { PermissionIndexComponent } from './permission-index/permission-index.component';
import { PermissionEditComponent } from './permission-edit/permission-edit.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    PermissionComponent,
    PermissionIndexComponent,
    PermissionEditComponent
  ],
  imports: [
    CommonModule,
    PermissionRoutingModule,
    FormsModule
  ]
})
export class PermissionModule { }
