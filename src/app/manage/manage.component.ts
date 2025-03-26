import { Component, OnInit, Renderer2 } from '@angular/core';
import { SidebarMenu, SystemUser } from '../@models/login.module';

@Component({
  selector: 'app-manage',
  templateUrl: './manage.component.html',
  styleUrls: ['./manage.component.scss']
})
export class ManageComponent implements OnInit {
  systemUser!: SystemUser;

  constructor(private renderer: Renderer2) { }
  

  // 初始化時要做什麼
  ngOnInit() {
    this.renderer.addClass(document.body, 'page-top');
  }

  // 要銷毀時要做什麼
  ngOnDestroy() {
    this.renderer.removeClass(document.body, 'page-top');
  }

   // 畫面置頂
  pageTop() {
    window.scrollTo(0, 0);
  }

}
