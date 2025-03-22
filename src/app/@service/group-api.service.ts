import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Group } from '../@models/group.model';
import { TodoResponse } from '../@models/todo.model';

@Injectable({
  providedIn: 'root'
})
export class GroupApiService {
  private url = '/angular/group_action';

  constructor(private http: HttpClient) { }

  // 取所有資料
  getGroupList() {
    return this.http.get<Group[]>(this.url);
  }

  // 新增多筆資料 -> 只給沒資料時使用
  addGroup() {
    return this.http.get<TodoResponse>(`${this.url}/create`);
  }

}
