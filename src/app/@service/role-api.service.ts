import { Injectable } from '@angular/core';
import { Role, RoleResponse } from '../@models/role.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class RoleApiService {

  private url: string = "/angular/role_action";

  constructor(private http: HttpClient) { }
  
    // 取得一筆資料
    getRoleByCode(code: string) {
      return this.http.get<RoleResponse>(`${this.url}/${code}`);
    }
  
    // 修改一筆資料
    updateRole(item: Role) {
      return this.http.put<RoleResponse>(`${this.url}/${item.code}`, item);
    }
}
