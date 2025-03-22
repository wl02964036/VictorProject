import { ActivatedRoute } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { TodoService } from 'src/app/@service/todo.service';

@Component({
  selector: 'app-todo-content',
  templateUrl: './todo-content.component.html',
  styleUrls: ['./todo-content.component.scss']
})
export class TodoContentComponent implements OnInit {
  title = 'OneTodo';
  placeholder = "What needs to be done??"
  constructor(private todoService: TodoService, private route: ActivatedRoute) { }

  ngOnInit() {
    this.todoService.dataList = [];
    this.route.paramMap.subscribe(data => {
      this.todoService.groupId = data.get("groupId") as string;
      this.todoService.getData();
    });

  }


}
