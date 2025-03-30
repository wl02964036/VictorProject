import { Component, OnInit } from '@angular/core';
import { AssetLoaderService } from 'src/app/@service/asset-loader.service';

@Component({
  selector: 'app-permission-index',
  templateUrl: './permission-index.component.html',
  styleUrls: ['./permission-index.component.scss']
})
export class PermissionIndexComponent implements OnInit {
  message?: string | null = null;
  queryUsername: string = '';

  constructor(private assetLoader: AssetLoaderService) { }

  ngOnInit(): void {
    this.message = history.state.message || null;
  }

  ngAfterViewInit(): void {
    // 1. 先載入 jQuery + DataTables Core
    this.assetLoader.loadAssets([
      'assets/sb-admin/vendor/datatables/jquery.dataTables.min.js'
    ], []).then(() => {
      // 2. 再載入 bootstrap4 套件 + 其他 JS + CSS
      return this.assetLoader.loadAssets(
        [
          'assets/sb-admin/vendor/datatables/dataTables.bootstrap4.min.js',
          'assets/sb-admin/vendor/datatables/springify.js'
        ],
        [
          'assets/sb-admin/vendor/datatables/dataTables.bootstrap4.min.css'
        ]
      );
    }).then(() => {
      // 3. 等全部載入完成後初始化
      this.initDataTable();
    });
  }

  initDataTable() {
    var dataTable = $("#dataTable").DataTable({
      language: {
        url: "assets/sb-admin/vendor/datatables/Chinese-traditional.json"
      },
      processing: true,
      serverSide: true,
      searching: false,
      ajax: {
        url: "/angular/permission/query",
        type: "POST",
        headers: { 'Authorization': 'Bearer ' + localStorage.getItem('token') || '' },
        data: this.springify,
      },
      order: [],
      columns: [
        {
          name: "index",
          data: null,
          width: "10%",
          orderable: false,
          searchable: false,
          render: function (data: any, type: any, row: any, meta: any) {
            var length = dataTable.page.len();
            var pageNumber = dataTable.page.info().page;
            var offset = pageNumber * length;
            return offset + meta.row + 1;
          },
        },
        {
          name: 'unitName',
          data: 'unitName',
          orderable: false,
          searchable: false
        },
        {
          name: 'displayName',
          data: 'displayName',
          orderable: false,
          searchable: false
        },
        {
          name: 'username',
          data: 'username',
          width: '20%',
          orderable: true,
          searchable: true,
          render: (data: string, type: any, row: any, meta: any) => {
            return `<a href="/manage/permission/edit?username=${data}" title="修改權限">${data}</a>`;
          }
        },
        {
          name: "roles",
          data: "roles",
          orderable: false,
          searchable: true,
        }
      ]
    });
  }

  springify(params: any, settings?: any): any {
    // 處理 columns
    if (Array.isArray(params.columns)) {
      params.columns.forEach((column: any, index: number) => {
        params[`columns[${index}].index`] = index;
        params[`columns[${index}].data`] = column.data;
        params[`columns[${index}].name`] = column.name;
        params[`columns[${index}].searchable`] = column.searchable;
        params[`columns[${index}].orderable`] = column.orderable;
        params[`columns[${index}].search.regex`] = column.search?.regex;
        params[`columns[${index}].search.value`] = column.search?.value;
      });
      delete params.columns;
    }

    // 處理 order
    if (Array.isArray(params.order)) {
      params.order.forEach((order: any, index: number) => {
        params[`order[${index}].column`] = order.column;
        params[`order[${index}].dir`] = order.dir;
      });
      delete params.order;
    }

    // 處理 search
    if (params.search) {
      params["search.regex"] = params.search.regex;
      params["search.value"] = params.search.value;
      delete params.search;
    }

    // 加上 filter form 的資料
    var filterForm = $("#filterForm");
    // 取得搜尋的參數

    var queryUsername = $("#queryUsername").val();
    if (queryUsername && queryUsername !== "") {
      params['queryUsername'] = queryUsername;
    }

    return params;
  }

  ngOnDestroy(): void {
    const table = $('#dataTable').DataTable();
    table.destroy(true); // true 表示連同 DOM 一起移除 DataTable 設定
  }

  onQuery() {
    var dataTable = $("#dataTable").DataTable();
    dataTable.ajax.reload();
  }

  onReset() {
    this.queryUsername = '';
  }

}
