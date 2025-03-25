import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MenuComponent } from '../shared/menu/menu.component';
import { Header2Component } from '../shared/header2/header2.component';
import { ManageRoutingModule } from './manage-routing.module';
import { HomeComponent } from './home/home.component';
import { ManageComponent } from './manage.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    MenuComponent,
    Header2Component,
    HomeComponent,
    ManageComponent,
  ],
  imports: [
    CommonModule,
    ManageRoutingModule,
    FormsModule,
  ]
})
export class ManageModule { }
