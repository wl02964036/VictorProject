import { Injectable } from '@angular/core';
import { Role, RoleResponse } from '../@models/role.model';
import { HttpClient } from '@angular/common/http';
import { BaseResponse, TreeNodeModel } from '../@models/base.model';
import { environment } from 'src/environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class RoleApiService {

  private url: string = `/angular/role`;

  constructor(private http: HttpClient) { }

  // 取得Menu Tree
  getMenuTree(node: string) {
    return this.http.get<TreeNodeModel>(`/angular/tree/menus`, { params: { node } });
  }

  // 取得一筆資料
  checkCodeRepeat(code: string) {
    return this.http.get(`${this.url}/codeRepeat`, { responseType: 'text', params: { code } });
  }

  // 修改一筆資料
  createRole(item: Role) {
    return this.http.post<RoleResponse>(`${this.url}/create`, item);
  }

  // 取得一筆資料
  getRoleByCode(code: string) {
    return this.http.get<RoleResponse>(`${this.url}/edit`, { params: { code } });
  }

  // 修改一筆資料
  updateRole(item: Role) {
    return this.http.post<RoleResponse>(`${this.url}/update`, item);
  }

  // 刪除一筆資料
  deleteRoles(roles: string) {
    return this.http.post<BaseResponse>(`${this.url}/destroy`, null, { params: { roles } });
  }
}
