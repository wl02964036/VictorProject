import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { Todo, TodoStatusType } from 'src/app/@models/todo.model';
import { TodoService } from 'src/app/@service/todo.service';

@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent implements OnInit {
  TodoStatusType = TodoStatusType;

  // getter
  get todoActive(): Todo[] {
    return this.todoService.todoActive;
  }

  get currentStatus(): TodoStatusType {
    return this.todoService.currentStatus;
  }

  constructor(private todoService: TodoService,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    this.route.paramMap.subscribe(data => {
      const action = data.get('action');
      if (action === TodoStatusType.All.toString()) {
        this.todoService.setTodoStatusType(TodoStatusType.All);
      } else if (action === TodoStatusType.Active.toString()) {
        this.todoService.setTodoStatusType(TodoStatusType.Active);
      } else if (action === TodoStatusType.Completed.toString()) {
        this.todoService.setTodoStatusType(TodoStatusType.Completed);
      }
    })
  }

  clearCompleted() {
    this.todoService.clearCompleted();
  }

  setTodoStatusType(status: TodoStatusType) {
    this.todoService.setTodoStatusType(status);
  }

}
