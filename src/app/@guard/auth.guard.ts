import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  errorMessage: string | null = null;

  constructor(private router: Router) {}
  
  // 只針對父親的路由守衛
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    const token = localStorage.getItem('token');
    if (token) {
      try {
        const payload = JSON.parse(window.atob(token.split('.')[1]));
        const exp = new Date(Number(payload.exp) * 1000);
        if (new Date() < exp) {
          return true;
        }else {
          this.errorMessage = 'JWT已過期，請重新登入';
          alert(this.errorMessage);
        }
      } catch (err) {
        this.errorMessage = 'JWT 解析錯誤';
        console.error(this.errorMessage, err);
        alert(this.errorMessage);
      }
    } else {
      this.errorMessage = 'JWT失效，請重新登入';
      alert(this.errorMessage);
    }


    // 清掉 token 並導向登入頁
    localStorage.removeItem('token');
    localStorage.removeItem("systemUser");
    localStorage.removeItem("sidebarMenus");
    this.router.navigate(['/login'], { queryParams: { error: this.errorMessage } });
    return false;
  }

  // 針對兒子的路由守衛
  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    // 這裡直接回到父親的路由守衛的方法即可
    return this.canActivate(childRoute, state);
  }

}
