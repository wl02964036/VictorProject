import { Component } from '@angular/core';
import { Todo, TodoStatusType } from 'src/app/@models/todo.model';
import { TodoService } from 'src/app/@service/todo.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {
  TodoStatusType = TodoStatusType;

  // getter
  get todoActive(): Todo[] {
    return this.todoService.todoActive;
  }

  get currentStatus(): TodoStatusType {
    return this.todoService.currentStatus;
  }

  constructor(private todoService: TodoService) { }

  clearCompleted() {
    this.todoService.clearCompleted();
  }

  setTodoStatusType(status: TodoStatusType) {
    this.todoService.setTodoStatusType(status);
  }
  
}
