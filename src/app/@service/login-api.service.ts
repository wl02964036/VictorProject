import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { LoginPost, LoginResponse } from '../@models/login.module';
import { catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class LoginApiService {
  private url = '/angular/login';

  constructor(private http: HttpClient) { }

  jwtLogin(item: LoginPost) {
    return this.http.post<LoginResponse>(`${this.url}`, item).pipe(
      catchError(error => {
        console.error('帳號登入失敗', error);
        // 可以視情況回傳 null、空物件、錯誤物件等
        return of({ status: 'error', message: '帳號登入失敗' } as LoginResponse);
      })
    );
  }
}
