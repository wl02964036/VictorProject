import { Component, Input } from '@angular/core';
import { TodoService } from 'src/app/@service/todo.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent {
  @Input()
  title!: string;
  @Input()
  placeholder!: string;
  todoInputModel!: string;

  constructor(private todoService: TodoService) { }

  add() {
    this.todoService.add(this.todoInputModel);
    this.todoInputModel = "";
  }

}
