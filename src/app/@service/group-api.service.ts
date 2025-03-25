import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Group } from '../@models/group.model';
import { TodoResponse } from '../@models/todo.model';
import { catchError, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class GroupApiService {
  private url = '/angular/group_action';

  constructor(private http: HttpClient) { }

  // 取所有資料
  getGroupList() {
    return this.http.get<Group[]>(this.url).pipe(
      catchError(error => {
        console.error('無法取得群組列表', error);
        return of([]);
      })
    );
  }

  // 新增多筆資料 -> 只給沒資料時使用
  addGroup() {
    return this.http.get<TodoResponse>(`${this.url}/create`).pipe(
      catchError(error => {
        console.error('建立群組失敗', error);
        // 可以視情況回傳 null、空物件、錯誤物件等
        return of({ status: 'error', message: '建立群組失敗' } as TodoResponse);
      })
    );
  }

}
