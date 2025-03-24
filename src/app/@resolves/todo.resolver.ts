import { Injectable } from '@angular/core';
import {
  Router, Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { Observable } from 'rxjs';
import { TodoApiService } from '../@service/todo-api.service';
import { TodoService } from '../@service/todo.service';
import { Todo } from '../@models/todo.model';

@Injectable({
  providedIn: 'root'
})
export class TodoResolver implements Resolve<Todo[]> {

  constructor(private todoApiService: TodoApiService, private todoService: TodoService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Todo[]> {
    const groupId = route.paramMap.get("groupId") as string;
    this.todoService.groupId = groupId;
    return this.todoApiService.getTodoList(groupId);
  }
}
