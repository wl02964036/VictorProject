import { Component, OnInit } from '@angular/core';
import { AssetLoaderService } from 'src/app/@service/asset-loader.service';

@Component({
  selector: 'app-login-log-index',
  templateUrl: './login-log-index.component.html',
  styleUrls: ['./login-log-index.component.scss']
})
export class LoginLogIndexComponent implements OnInit {
  message?: string | null = null;
  queryUsername: string = '';
  queryActionedAt: Date | null = null;

  constructor(private assetLoader: AssetLoaderService) { }

  ngOnInit(): void {
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
          'assets/sb-admin/vendor/datatables/springify.js',
          'assets/sb-admin/vendor/bootstrap-datepicker/js/bootstrap-datepicker.min.js',
          'assets/sb-admin/vendor/bootstrap-datepicker/locales/bootstrap-datepicker.zh-TW.min.js'
        ],
        [
          'assets/sb-admin/vendor/datatables/dataTables.bootstrap4.min.css',
          'assets/sb-admin/vendor/bootstrap-datepicker/css/bootstrap-datepicker.standalone.min.css'
        ]
      );
    }).then(() => {
      // 3. 等全部載入完成後初始化
      this.initDataTable();
    });
  }

  initDataTable() {

    $("#queryActionedAt").datepicker({
      format: "yyyy-mm-dd",
      todayHighlight: true,
      language: "zh-TW",
    });

    var dataTable = $("#dataTable").DataTable({
      language: {
        url: "assets/sb-admin/vendor/datatables/Chinese-traditional.json"
      },
      processing: true,
      serverSide: true,
      searching: false,
      ajax: {
        url: "/angular/login-log/query",
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
          name: 'username',
          data: 'username',
          orderable: false,
          searchable: false
        },
        {
          name: 'actionedAt',
          data: 'actionedAt',
          width: "20%",
          orderable: false,
          searchable: false
        },
        {
          name: "ip",
          data: "ip",
          orderable: false,
          searchable: false,
        },
        {
          name: "status",
          data: "status",
          orderable: false,
          searchable: false,
          render: function (data: any, type: any, row: any, meta: any) {
            if (data === "succeed") {
                return "成功";
            } else if (data === "failed") {
                return "失敗";
            } else {
                return data;
            }
          },
        },
        {
            name: "cause",
            data: "cause",
            orderable: false,
            searchable: false,
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

    var queryActionedAt = $("#queryActionedAt").val();

    if (queryActionedAt && queryActionedAt !== "") {
      params['queryActionedAt'] = queryActionedAt;
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
    this.queryActionedAt = null;
    $("#queryActionedAt").val(this.queryActionedAt);
  }

}
