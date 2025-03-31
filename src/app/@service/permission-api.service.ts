import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BaseResponse } from '../@models/base.model';
import { GetPermissionResponse } from '../@models/permission.model';
import { User } from '../@models/organization.model';
import { environment } from 'src/environments/environment.prod';

@Injectable({
  providedIn: 'root'
})
export class PermissionApiService {

  private url: string = `/angular/permission`;

  constructor(private http: HttpClient) { }
  
    // 取得一筆資料
    getPermissionForUpdate(username: string) {
      return this.http.get<GetPermissionResponse>(`${this.url}/edit`, { params: { username } });
    }
  
    // 修改一筆資料
    updatePermission(item: User) {
      return this.http.post<BaseResponse>(`${this.url}/update`, item);
    }
}
