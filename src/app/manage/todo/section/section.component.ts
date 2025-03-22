import { Component, ViewChild } from '@angular/core';
import { Todo } from 'src/app/@models/todo.model';
import { TodoService } from 'src/app/@service/todo.service';
import { TodoInfoModalComponent } from './todo-info-modal/todo-info-modal.component';

@Component({
  selector: 'app-section',
  templateUrl: './section.component.html',
  styleUrls: ['./section.component.scss']
})
export class SectionComponent {
  nowSelectTodo!: Todo;
  @ViewChild(TodoInfoModalComponent)
  private todoInfoModalComponent!: TodoInfoModalComponent;

  // getter
  get toggleAllBtn(): boolean {
    return this.todoService.toggleAllBtn;
  }

  get currentTodoList(): Todo[] {
    return this.todoService.currentTodoList;
  }

  constructor(private todoService: TodoService) { }

  toggleAll() {
    this.todoService.toggleAll();
  }

  clickCheck(item: Todo) {
    this.todoService.clickCheck(item);
  }

  edit(item: Todo) {
    this.todoService.edit(item);
  }

  update(item: Todo) {
    this.todoService.update(item);
  }

  delete(item: Todo) {
    this.todoService.delete(item);
  }

  modalShow(item: Todo) {
    this.nowSelectTodo = item;
    this.todoInfoModalComponent.show()
  }

}
