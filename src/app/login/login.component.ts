import { Component, } from '@angular/core';
import { Router } from '@angular/router';
import { LoginApiService } from '../@service/login-api.service';
import { LoginPost } from '../@models/login.module';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent {
  loginValue: LoginPost = {
    username: '',
    pwd: ''
  }

  constructor(private router: Router, private loginApiService: LoginApiService) { }

  login() {
    this.loginApiService.jwtLogin(this.loginValue).subscribe(data => {
      if (data.status === 'success') {
        localStorage.setItem('token', data.token);
        this.router.navigateByUrl("/manage");
      }else {
        alert(data.message);
      }
    });
  }
}
