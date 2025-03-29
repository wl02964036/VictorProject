import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { OrganizationRoutingModule } from './organization-routing.module';
import { OrganizationComponent } from './organization.component';
import { OrganizationIndexComponent } from './organization-index/organization-index.component';
import { OrganizationEditUnitComponent } from './organization-edit-unit/organization-edit-unit.component';
import { OrganizationEditUserComponent } from './organization-edit-user/organization-edit-user.component';
import { FormsModule } from '@angular/forms';
import { OrganizationNewUnitComponent } from './organization-new-unit/organization-new-unit.component';
import { OrganizationNewUserComponent } from './organization-new-user/organization-new-user.component';
import { OrganizationEditPasswordComponent } from './organization-edit-password/organization-edit-password.component';


@NgModule({
  declarations: [
    OrganizationComponent,
    OrganizationIndexComponent,
    OrganizationEditUnitComponent,
    OrganizationEditUserComponent,
    OrganizationNewUnitComponent,
    OrganizationNewUserComponent,
    OrganizationEditPasswordComponent
  ],
  imports: [
    CommonModule,
    OrganizationRoutingModule,
    FormsModule
  ]
})
export class OrganizationModule { }
