import { Injectable } from '@angular/core';
import {
    Resolve,
    RouterStateSnapshot,
    ActivatedRouteSnapshot
} from '@angular/router';
import { catchError, map, Observable, throwError } from 'rxjs';
import { Role } from '../@models/role.model';
import { RoleApiService } from '../@service/role-api.service';

@Injectable({
    providedIn: 'root'
})
export class RoleEditResolver implements Resolve<Role> {

    constructor(private roleApiService: RoleApiService) { }

    resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Role> {
        const code = route.paramMap.get("code") as string;
        return this.roleApiService.getRoleByCode(code).pipe(
            map(response => {
                if (response.status === 'success') {
                    return response.role;
                } else {
                    throw new Error(response.message || '讀取角色資料失敗');
                }
            }),
            catchError(err => {
                // 可加上 logging
                console.error(err);
                return throwError(() => err); // 傳給 component 的 error handler
            })
        );
    }
}
