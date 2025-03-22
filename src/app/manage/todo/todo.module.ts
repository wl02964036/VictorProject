import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TodoRoutingModule } from './todo-routing.module';
import { FormsModule } from '@angular/forms';
import { TodoComponent } from './todo.component';
import { HeaderComponent } from './todo-content/header/header.component';
import { SectionComponent } from './todo-content/section/section.component';
import { FooterComponent } from './todo-content/footer/footer.component';
import { TodoListComponent } from './todo-list/todo-list.component';
import { TodoContentComponent } from './todo-content/todo-content.component';
import { TodoInfoModalComponent } from './todo-content/section/todo-info-modal/todo-info-modal.component';


@NgModule({
  declarations: [
    TodoComponent,
    HeaderComponent,
    SectionComponent,
    FooterComponent,
    TodoInfoModalComponent,
    TodoListComponent,
    TodoContentComponent,
  ],
  imports: [
    CommonModule,
    TodoRoutingModule,
    FormsModule,
  ]
})
export class TodoModule { }
