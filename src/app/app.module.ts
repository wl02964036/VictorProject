import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { TodoComponent } from './todo/todo.component';
import { HeaderComponent } from './todo/header/header.component';
import { FooterComponent } from './todo/footer/footer.component';
import { SectionComponent } from './todo/section/section.component';
import { TodoInfoModalComponent } from './todo/section/todo-info-modal/todo-info-modal.component';
import { MenuComponent } from './shared/menu/menu.component';
import { Header2Component } from './shared/header2/header2.component';
import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from './home/home.component';
import { NotfoundComponent } from './notfound/notfound.component';

@NgModule({
  declarations: [
    AppComponent,
    TodoComponent,
    HeaderComponent,
    FooterComponent,
    SectionComponent,
    TodoInfoModalComponent,
    MenuComponent,
    Header2Component,
    HomeComponent,
    NotfoundComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpClientModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
