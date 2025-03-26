import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CaptchaApiService {
  private url = '/angular';

  constructor(private http: HttpClient) { }
  
    // 重新載入驗證碼
    reloadCaptcha() {
      return this.http.get(`${this.url}/reloadCaptcha`, { responseType: 'text' }).pipe(
        catchError(error => {
          console.error('無法重新載入驗證碼', error);
          return of('');
        })
      );
    }

    // 播放驗證碼語音
    playCaptchaAudio() {
      return this.http.get('/angular/captchaNumber?t=' + Date.now(), { responseType: 'text' }).pipe(
        catchError(error => {
          console.error('無法播放驗證碼語音', error);
          return of('');
        })
      );
    }
}
