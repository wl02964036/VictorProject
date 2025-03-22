import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { MenuComponent } from '../shared/menu/menu.component';
import { Header2Component } from '../shared/header2/header2.component';
import { ManageRoutingModule } from './manage-routing.module';
import { TodoComponent } from './todo/todo.component';
import { HeaderComponent } from './todo/header/header.component';
import { FooterComponent } from './todo/footer/footer.component';
import { SectionComponent } from './todo/section/section.component';
import { TodoInfoModalComponent } from './todo/section/todo-info-modal/todo-info-modal.component';
import { HomeComponent } from './home/home.component';
import { ManageComponent } from './manage.component';
import { FormsModule } from '@angular/forms';


@NgModule({
  declarations: [
    MenuComponent,
    Header2Component,
    TodoComponent,
    HeaderComponent,
    FooterComponent,
    SectionComponent,
    TodoInfoModalComponent,
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
