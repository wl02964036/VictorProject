import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { AuditLogRoutingModule } from './audit-log-routing.module';
import { AuditLogComponent } from './audit-log.component';
import { FormsModule } from '@angular/forms';
import { AuditLogIndexComponent } from './audit-log-index/audit-log-index.component';


@NgModule({
  declarations: [
    AuditLogComponent,
    AuditLogIndexComponent
  ],
  imports: [
    CommonModule,
    AuditLogRoutingModule,
    FormsModule
  ]
})
export class AuditLogModule { }
