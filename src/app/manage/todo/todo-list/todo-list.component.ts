import { Component, OnInit } from '@angular/core';
import { Group } from 'src/app/@models/group.model';
import { GroupApiService } from 'src/app/@service/group-api.service';

@Component({
  selector: 'app-todo-list',
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.scss']
})
export class TodoListComponent implements OnInit {
  dataList: Group[] = [];

  constructor(private groupApiService: GroupApiService) { }

  ngOnInit(): void {
    this.getData();
  }

  getData() {
    this.groupApiService.getGroupList().subscribe(data => {
      this.dataList = data;
    });
  }

  add() {
    this.groupApiService.addGroup().subscribe(data => {
      if (data.status === "success") {
        this.getData();
      }else {
        alert(data.message);
      }
      return
    });
  }

}
