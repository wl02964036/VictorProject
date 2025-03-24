import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot, UrlTree } from '@angular/router';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  // 只針對父親的路由守衛
  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree {

    const jwt = localStorage.getItem('jwt');
    if (jwt) {
      const payload = JSON.parse(window.atob(jwt.split('.')[1]));
      const exp = new Date(Number(payload.exp) * 1000);
      if (new Date() > exp) {
        alert('JWT已過期，請重新登入');
        return false;
      }
    } else {
      alert('尚未登入');
      return false;
    }

    return true;
  }

  // 針對兒子的路由守衛
  canActivateChild(childRoute: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> | Promise<boolean | UrlTree> {
    // 這裡直接回到父親的路由守衛的方法即可
    return this.canActivate(childRoute, state);
  }

}
