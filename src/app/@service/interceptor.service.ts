import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest, HttpResponse } from '@angular/common/http';
import { Injectable, Injector } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, map, Observable, of, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InterceptorService implements HttpInterceptor {

  constructor(private injector: Injector, private router: Router) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {

    const token = localStorage.getItem('token');

    if (token) {
      req = req.clone({
        headers: req.headers.set('Authorization', 'Bearer ' + token)
      });
    }

    return next.handle(req).pipe(
      map(event => {
        if (event instanceof HttpResponse) {
          switch (event.body.status) {
            case 'success': {
              event = this.success(event);
              break;
            }
            case 'error': {
              // event = this.error(event);
              break;
            }
            case 'no_auth': {
              this.router.navigate(['/login'], { queryParams: { error: event.body.message } }); // 踢回登入頁
              break;
            }
          }
        }
        return event;
      }),
      catchError((error: HttpErrorResponse) => {
        // 如果後端直接丟出 HTTP 錯誤（如 401），這裡就能處理
        if (error.status === 401) {
          console.warn('401 Unauthorized', error);
          if (error.error?.status === 'no_auth') {
            this.router.navigate(['/login'], { queryParams: { error: error.error?.message } }); // 踢回登入頁
          }
        }

        // 要繼續拋出讓呼叫者能處理（例如 .catchError()）
        // return throwError(() => error);
        return of(new HttpResponse({ status: 200, 
          body: { status: error.error?.status ?? 'no_auth', message: error.error?.message ?? 'No Message!!' } }));;
      })
    );
  }

  private success(event: any): any {
    if (event.body) {
      return event.clone({ body: event.body });
    } else {
      return event.clone({ body: true });
    }

  }
}
