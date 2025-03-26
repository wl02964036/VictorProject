import { Component, OnInit } from '@angular/core';
import { SidebarMenu, SystemUser } from 'src/app/@models/login.module';

@Component({
  selector: 'app-side-bar-menu',
  templateUrl: './sidebar-menu.component.html',
  styleUrls: ['./sidebar-menu.component.scss']
})
export class SideBarMenuComponent implements OnInit {
  sidebarMenus: SidebarMenu[] = [];

  constructor() {  }

  ngOnInit(): void {
    const menuData = localStorage.getItem('sidebarMenus');

    if (menuData) {
      try {
        this.sidebarMenus = JSON.parse(menuData) as SidebarMenu[];
      } catch (e) {
        console.error('解析 SidebarMenu[] 失敗：', e);
      }
    }
  }

}
