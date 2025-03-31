import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { LoginLogRoutingModule } from './login-log-routing.module';
import { FormsModule } from '@angular/forms';
import { LoginLogComponent } from './login-log.component';
import { LoginLogIndexComponent } from './login-log-index/login-log-index.component';


@NgModule({
  declarations: [
    LoginLogComponent,
    LoginLogIndexComponent
  ],
  imports: [
    CommonModule,
    LoginLogRoutingModule,
    FormsModule
  ]
})
export class LoginLogModule { }
