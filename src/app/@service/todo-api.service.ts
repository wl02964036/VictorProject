import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Todo, TodoResponse } from '../@models/todo.model';

@Injectable({
  providedIn: 'root'
})
export class TodoApiService {

  private url: string = "/angular/todo_action";

  constructor(private http: HttpClient) { }

  // 取所有資料
  getTodoList(groupId: string) {
    return this.http.get<Todo[]>(`${this.url}/${groupId}`);
  }

  // 新增一筆資料
  addTodo(item: Todo) {
    return this.http.post<TodoResponse>(this.url, item);
  }

  // 修改一筆資料
  updateTodo(item: Todo) {
    return this.http.put<TodoResponse>(`${this.url}/${item.todoId}`, item)
  }

  // 全部狀態統一
  updateAllTodoStatus(status: boolean, groupId: string) {
    return this.http.put<TodoResponse>(`${this.url}/${groupId}/status`, status);
  }

  // 刪除一筆資料
  deleteTodo(item: Todo) {
    return this.http.delete<TodoResponse>(`${this.url}/${item.todoId}`);
  }

  // 刪除已完成的資料
  deleteCompletedTodo(groupId: string) {
    return this.http.delete<TodoResponse>(`${this.url}/${groupId}/clear_completed`);
  }
}
