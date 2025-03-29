import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { GetPasswordResponse, GetUnitResponse, GetUserResponse, PasswordModel, SavePasswordResponse, SaveUnitResponse, SaveUserResponse, Unit, User } from '../@models/organization.model';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrganizationApiService {

  private url: string = "/angular/organization";

  constructor(private http: HttpClient) { }

  // 取得Menu Tree
  getMenuTree(node: string) {
    return this.http.get<GetUnitResponse>(`/angular/tree/organization`, { params: { node } });
  }

  // 取得一筆資料
  getUnitForUpdate(node: string) {
    return this.http.get<GetUnitResponse>(`${this.url}/editUnit`, { params: { node } });
  }

  // 取得一筆資料
  getUnitForCreate(node: string) {
    return this.http.get<GetUnitResponse>(`${this.url}/newUnit`, { params: { node } });
  }

  // 取得一筆資料
  checkUnitCodeRepeat(code: string) {
    return this.http.get(`${this.url}/unitCodeRepeat`, { responseType: 'text', params: { code } });
  }

  // 新增一筆資料
  createUnit(item: Unit) {
    return this.http.post<SaveUnitResponse>(`${this.url}/createUnit`, item);
  }

  // 修改一筆資料
  updateUnit(item: Unit) {
    return this.http.post<SaveUnitResponse>(`${this.url}/updateUnit`, item);
  }

  // 刪除一筆資料
  deleteUnit(code: string) {
    return this.http.post<SaveUnitResponse>(`${this.url}/destroyUnit`, null, { params: { code: code } });
  }

  // 取得一筆資料
  getUserForUpdate(node: string) {
    return this.http.get<GetUserResponse>(`${this.url}/editUser`, { params: { node } });
  }

  // 取得一筆資料
  getUserForCreate(node: string) {
    return this.http.get<GetUserResponse>(`${this.url}/newUser`, { params: { node } });
  }

  // 取得一筆資料
  checkUsernameRepeat(username: string) {
    return this.http.get(`${this.url}/usernameRepeat`, { responseType: 'text', params: { username } });
  }

  // 新增一筆資料
  createUser(item: User) {
    return this.http.post<SaveUserResponse>(`${this.url}/createUser`, item);
  }

  // 修改一筆資料
  updateUser(item: User) {
    return this.http.post<SaveUserResponse>(`${this.url}/updateUser`, item);
  }

  // 刪除一筆資料
  deleteUser(username: string, unitCode: string) {
    return this.http.post<SaveUnitResponse>(`${this.url}/destroyUser`, null, { params: { username, unitCode } });
  }

  // 取得一筆資料
  getPasswordForUpdate(username: string) {
    return this.http.get<GetPasswordResponse>(`${this.url}/editPassword`, { params: { username } });
  }

  // 修改一筆資料
  updatePassword(item: PasswordModel) {
    return this.http.post<SavePasswordResponse>(`${this.url}/updatePassword`, item);
  }

  // 解除密碼錯誤鎖定
  unlockErrorPassword(username: string) {
    return this.http.post(`${this.url}/unlock`, null, { responseType: 'text', params: { username } });
  }

  // 匯出excel
  downloadReport(): Observable<Blob> {
    return this.http.post(`${this.url}/report`, null, {
      responseType: 'blob', // 👈 取得 binary 資料
      headers: new HttpHeaders({ 'Accept': 'application/vnd.ms-excel' })
    });
  }
}
