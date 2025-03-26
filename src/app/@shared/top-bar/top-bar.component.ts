import { Component, OnInit, Renderer2 } from '@angular/core';
import { Router } from '@angular/router';
import { SystemUser } from 'src/app/@models/login.module';

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.scss']
})
export class TopBarComponent implements OnInit {
  systemUser!: SystemUser;

  constructor(private router: Router) { }

  ngOnInit(): void {
    const user = localStorage.getItem('systemUser');

    if (user) {
      try {
        this.systemUser = JSON.parse(user) as SystemUser;
      } catch (e) {
        console.error('解析 SystemUser 失敗：', e);
      }
    }
  }
  
  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("systemUser");
    localStorage.removeItem("sidebarMenus");
    this.router.navigate(['/login'], { queryParams: { logout: true } });
  }

}
