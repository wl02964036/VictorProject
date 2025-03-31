import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { ManageRoutingModule } from './manage-routing.module';
import { ManageComponent } from './manage.component';
import { FormsModule } from '@angular/forms';
import { SideBarMenuComponent } from '../@shared/sidebar-menu/sidebar-menu.component';
import { TopBarComponent } from '../@shared/top-bar/top-bar.component';
import { IndexComponent } from './index/index.component';


@NgModule({
  declarations: [
    SideBarMenuComponent,
    TopBarComponent,
    IndexComponent,
    ManageComponent
  ],
  imports: [
    CommonModule,
    ManageRoutingModule,
    FormsModule,
  ]
})
export class ManageModule { }
