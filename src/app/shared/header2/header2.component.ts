import { Component } from '@angular/core';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header2',
  templateUrl: './header2.component.html',
  styleUrls: ['./header2.component.scss']
})
export class Header2Component {

  constructor(private router: Router) { }

  logout() {
    localStorage.removeItem("token");
    this.router.navigateByUrl("/login");
  }
}
