import { Injectable } from '@angular/core';
import { Todo, TodoClass, TodoStatusType } from '../@models/todo.model';
import { TodoApiService } from './todo-api.service';

@Injectable({
  providedIn: 'root'
})
export class TodoService {
  toggleAllBtn = false;
  currentStatus = TodoStatusType.All;
  dataList: Todo[] = [];
  groupId = '';
  
  // getter
  get currentTodoList(): Todo[] {
    let list: Todo[] = [];

    switch (this.currentStatus) {
      case TodoStatusType.Active:
        list = this.todoActive;
        break;
      case TodoStatusType.Completed:
        list = this.todoCompleted;
        break;
      default:
        list = this.dataList;
        break;
    }
    return list;
  }

  get todoActive(): Todo[] {
    return this.dataList.filter(item => !item.value);
  }

  get todoCompleted(): Todo[] {
    return this.dataList.filter(item => item.value);
  }

  constructor(private todoApiService: TodoApiService) {
  }

  getData() {
    this.todoApiService.getTodoList(this.groupId).subscribe(data => {
      this.dataList = data;
      this.dataList.forEach(item => {
        item.canEdit = true;
      });
      this.ready();
    });
  }

  ready() {
    this.checkToggleAllBtn();
  }

  add(inputValue: string) {
    const seqno = new Date().getTime();
    const todo: Todo = new TodoClass(inputValue, seqno);
    todo.groupId = this.groupId;
    this.dataList.push(todo);
    this.todoApiService.addTodo(todo).subscribe(data => {
      if (data.status === "success") {
        this.dataList.filter(item => item.seqno === seqno).map(item => {
          item.todoId = data.todoId;
          item.canEdit = true;
        });
      } else {
        this.dataList = this.dataList.filter(item => item.seqno !== seqno);
        alert(data.message);
      }
      return
    });
  }

  update(item: Todo) {
    this.updateApi(item);
    item.editing = false;
    return
  }

  clickCheck(item: Todo) {
    item.value = !item.value;
    this.checkToggleAllBtn();
    this.updateApi(item);
  }

  delete(item: Todo) {
    this.deleteApi(item);
    this.dataList = this.dataList.filter(data => data.name !== item.name);
  }

  toggleAll() {
    this.toggleAllBtn = !this.toggleAllBtn;

    this.dataList.forEach(item => {
      item.value = this.toggleAllBtn;
    });
    this.updateAllStatusApi(this.toggleAllBtn, this.groupId);
  }

  clearCompleted() {
    this.deleteCompletedApi(this.groupId);
    this.dataList = this.todoActive;
  }

  edit(item: Todo) {
    if (item.canEdit) {
      item.editing = !item.editing;
    }
  }

  setTodoStatusType(status: TodoStatusType) {
    this.currentStatus = status;
  }

  checkToggleAllBtn() {
    if (this.dataList.length === this.todoCompleted.length) {
      this.toggleAllBtn = true;
    } else {
      this.toggleAllBtn = false;
    }
  }

  //do api
  updateApi(item: Todo) {
    this.todoApiService.updateTodo(item).subscribe(data => {
      if (data.status !== "success") {
        alert(data.message);
      }
    });
  }

  updateAllStatusApi(status: boolean, groupId: string) {
    this.todoApiService.updateAllTodoStatus(status, groupId).subscribe(data => {
      if (data.status !== "success") {
        alert(data.message);
      }
    });
  }

  deleteApi(item: Todo) {
    this.todoApiService.deleteTodo(item).subscribe(data => {
      if (data.status !== "success") {
        alert(data.message);
      }
    });
  }

  deleteCompletedApi(groupId: string) {
    this.todoApiService.deleteCompletedTodo(groupId).subscribe(data => {
      if (data.status !== "success") {
        alert(data.message);
      }
    });
  }
}
