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
    // 清空既有的資料
    this.todoService.dataList = [];

    // 預先載入資料Resolve
    this.route.data.subscribe(data => {
      this.todoService.dataList = data["dataList"];
      this.todoService.ready();
    });
  }
}
