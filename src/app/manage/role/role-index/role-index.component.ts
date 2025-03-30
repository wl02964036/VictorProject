import { ActivatedRoute, Router } from '@angular/router';
import { AfterViewInit, Component, EventEmitter, Input, OnDestroy, OnInit, Output } from '@angular/core';
import { AssetLoaderService } from 'src/app/@service/asset-loader.service';
import { RoleApiService } from 'src/app/@service/role-api.service';

@Component({
  selector: 'app-role-index',
  templateUrl: './role-index.component.html',
  styleUrls: ['./role-index.component.scss']
})
export class RoleIndexComponent implements OnInit, AfterViewInit, OnDestroy {
  message?: string | null = null;
  queryTitle: string = '';

  constructor(private assetLoader: AssetLoaderService, private roleApiService: RoleApiService, private router: Router, private route: ActivatedRoute) { }

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
        url: "/angular/role/query",
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
          name: "checkbox",
          data: null,
          orderable: false,
          searchable: false,
          render: function (data: any, type: any, row: any, meta: any) {
            return '<div class="form-check">' +
              '<input type="checkbox" name="selectedItem" class="form-check-input" data-code="' + row.code + '">' +
              '</div>';
          },
        },
        {
          name: "code",
          data: "code",
          orderable: true,
          searchable: false,
          render: function (data: any, type: any, row: any, meta: any) {
            return "<a href='/manage/role/edit?code=" + row.code + "'>" + data + "</a>";
          }
        },
        {
          name: "title",
          data: "title",
          orderable: false,
          searchable: true,
        },
        {
          name: "updatedBy",
          data: "updatedBy",
          orderable: false,
          searchable: false,
        },
        {
          name: "updateUnit",
          data: "updateUnit",
          orderable: false,
          searchable: false,
        },
        {
          name: "updatedAt",
          data: "updatedAt",
          orderable: false,
          searchable: false,
        },
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

    var queryTitle = $("#queryTitle").val();
    if (queryTitle && queryTitle !== "") {
      params['queryTitle'] = queryTitle;
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
    this.queryTitle = '';
  }

  onDestroy() {
    const selected = $("input[name='selectedItem']:checked");
    var size = selected.length;
    if (size < 1) {
      alert("請至少勾選一項要刪除的項目");
    } else {
      const items: string[] = [];
      selected.each((_: any, element: any) => {
        items.push($(element).data("code"));
      });
      if (confirm("確定要刪除這些項目？")) {
        var roles: string = '';
        roles = items.join(",");
        this.roleApiService.deleteRoles(roles).subscribe(data => {
          this.message = data.message;
          this.onQuery();
        });
      }
    }
  }

  onNewRole() {
    this.router.navigate(['../new'], { relativeTo: this.route });
  }

}
