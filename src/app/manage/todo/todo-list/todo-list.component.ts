import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Group } from 'src/app/@models/group.model';
import { GroupApiService } from 'src/app/@service/group-api.service';

@Component({
  selector: 'app-todo-list',
  templateUrl: './todo-list.component.html',
  styleUrls: ['./todo-list.component.scss']
})
export class TodoListComponent implements OnInit {
  dataList: Group[] = [];

  constructor(private groupApiService: GroupApiService, private route: ActivatedRoute) { }

  ngOnInit(): void {
    // 清空既有的資料
    this.dataList = [];

    // 預先載入資料Resolve
    this.getResolveData();
  }

  getResolveData() {
    this.route.data.subscribe(data => {
      this.dataList = data["dataList"];
    });
  }

  add() {
    this.groupApiService.addGroup().subscribe(data => {
      if (data.status === "success") {
        // 預先載入資料Resolve
        this.getResolveData();
      }else {
        alert(data.message);
      }
      return
    });
  }

}
