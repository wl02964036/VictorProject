import { HttpClient } from '@angular/common/http';
import { Component, OnInit, Renderer2, } from '@angular/core';
import { Router } from '@angular/router';
import { LoginApiService } from '../@service/login-api.service';
import { LoginPost } from '../@models/login.module';
import { CaptchaApiService } from '../@service/captcha-api.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  loginValue: LoginPost = {
    username: '',
    password: '',
    captcha: ''
  }
  errorMessage?: string | null = null;
  loggedOut = false;
  isIE = false;

  constructor(private router: Router, 
    private loginApiService: LoginApiService, 
    private captchaApiService: CaptchaApiService, 
    private renderer: Renderer2) { 
      // 載入驗證碼
      this.reloadCaptcha();
    }

  // 初始化時要做什麼
  ngOnInit() {
    this.renderer.addClass(document.body, 'bg-gradient-primary');

    this.detectIE();

    const params = new URLSearchParams(window.location.search);
    if (params.has('logout')) {
      this.loggedOut = true;
    } else if (params.has('error')) {
      this.errorMessage = params.get('error');
    }

    // 防止嵌入 iframe
    if (window.top !== window.self) {
      window.top!.location.href = window.location.href;
    }

    // from表單驗證
    $("#loginForm").parsley({
      errorClass: "is-invalid",
      successClass: "is-valid",
      classHandler: function (ParsleyField: any) {
          return ParsleyField.$element;
      },
      errorsWrapper: '<div class="invalid-feedback"></div>',
      errorTemplate: "<div></div>"
    });
  }

  // 要銷毀時要做什麼
  ngOnDestroy() {
    this.renderer.removeClass(document.body, 'bg-gradient-primary');
  }

  detectIE() {
    const ua = window.navigator.userAgent;
    this.isIE = ua.indexOf('MSIE') > -1 || ua.indexOf('Trident') > -1;
  }

  reloadCaptcha() {
    this.captchaApiService.reloadCaptcha().subscribe(() => {
      const img = document.getElementById('captchaImg') as HTMLImageElement;
      img.src = `/angular/captchaImage?t=${Date.now()}`;

      const audio = document.getElementById('captchaAudioWav') as HTMLAudioElement;
      audio.src = `/angular/captchaAudio`;
    });
  }

  playCaptchaAudio() {
    this.captchaApiService.playCaptchaAudio().subscribe((data) => {
      window.open(`/wav/${data}.wav`, '_blank');
    });
  }

  onSubmit(form: any) {
    if (form.valid) {
      this.loginApiService.jwtLogin(this.loginValue).subscribe(data => {
        if (data.status === 'success') {
          localStorage.setItem('token', data.token);
          localStorage.setItem('systemUser', JSON.stringify(data.systemUser));
          localStorage.setItem('sidebarMenus', JSON.stringify(data.sidebarMenus));
          this.router.navigateByUrl("/manage");
        }else {
          this.errorMessage = data.message;
        }
      });
    }
  }
}
