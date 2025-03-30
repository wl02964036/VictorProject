import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Role } from 'src/app/@models/role.model';
import { AssetLoaderService } from 'src/app/@service/asset-loader.service';
import { RoleApiService } from 'src/app/@service/role-api.service';

@Component({
  selector: 'app-role-edit',
  templateUrl: './role-edit.component.html',
  styleUrls: ['./role-edit.component.scss']
})
export class RoleEditComponent implements OnInit {
  message?: string | null = null;
  code: string = '';
  formInstance: any;
  originRole!: Role;
  role: Role = {
    code: '',
    title: '',
    description: '',
    assignable: false,
    items: []
  };
  assignableOptions = [{ id: 1, value: true, label: '是' }, { id: 2, value: false, label: '否' }];

  constructor(private roleApiService: RoleApiService, private assetLoader: AssetLoaderService, private router: Router, private route: ActivatedRoute) { }

  ngOnInit(): void {
    const params = new URLSearchParams(window.location.search);
    if (params.has('code')) {
      this.code = params.get('code') as string;
    }

    this.roleApiService.getRoleByCode(this.code).subscribe(data => {
      if (data.status === "success") {
        this.originRole = { ...data.role };
        this.role = { ...data.role };

        // 若已初始化過 parsley，要重置
        if (this.formInstance) {
          this.formInstance.reset();
        }
      } else {
        this.message = data.message;
      }
    });
  }

  ngAfterViewInit() {
    // 載入 css & js 後，再執行init操作
    this.assetLoader.loadAssets(
      [
        'assets/sb-admin/vendor/jstree/jstree.min.js',
        'assets/sb-admin/vendor/application/customValidator.js'
      ],
      [
        'assets/sb-admin/vendor/jstree/themes/default/style.min.css'
      ]
    ).then(() => {
      this.initJsTreeAndVaildate();
      this.initForm();
    });
  }

  initForm() {
    const self = this;
    // from表單驗證
    self.formInstance = $("#roleForm").parsley({
      errorClass: "is-invalid",
      successClass: "is-valid",
      classHandler: function (ParsleyField: any) {
        return ParsleyField.$element;
      },
      errorsWrapper: '<div class="invalid-feedback"></div>',
      errorTemplate: "<div></div>"
    });
  }

  initJsTreeAndVaildate() {
    const self = this;

    $("#menuTree")
      .jstree({
        core: {
          data: function (node: any, cb: any) {
            self.roleApiService.getMenuTree(node.id).subscribe({
              next: (data: any) => {
                cb(data); // 傳給 jsTree
              },
              error: (err: any) => {
                console.error('jsTree error', err);
              }
            });
          },
          multiple: false,
          themes: {
            dots: false,
            stripes: false,
            responsive: true,
            variant: 'large'
          },
          check_callback: false
        },
        checkbox: {
          three_state: false,
          whole_node: false,
          tie_selection: false
        },
        plugins: ['checkbox']
      })
      .on('loaded.jstree', () => {
        $('#menuTree').jstree('open_all');
        return false;
      })
      .on('load_node.jstree', (e: any, data: any) => {
        const node = data.node;
        $.each(node.children, (index: number, elem: any) => {
          if (_.includes(this.role.items, elem)) {
            $('#menuTree').jstree('check_node', elem);
          }
        });
        return false;
      })
      .on('refresh.jstree', () => {
        $('#menuTree').jstree('open_all');
        return false;
      });

      (window as any).Parsley.on('form:validated', function (formInstance: any) {
      let validated = true;

      if (!formInstance.isValid()) {
        validated = false;
      }

      const length = self.role.items.length;
      if (length < 1) {
        $('div.menuError').html('必需勾選一項以上的應用功能').show();
        validated = false;
      } else {
        $('div.menuError').hide();
      }

      if (!validated) {
        formInstance.validationResult = false;
      }
    });
  }

  onSubmit(): void {
    const self = this;
    
    // 檢核jsTree
    this.role.items = [];

    const items = $('#menuTree').jstree('get_checked', true);
    $.each(items, function (index: number, elem: any) {
      self.role.items.push(elem.id);
    });

    // 進行整體 Parsley 驗證
    if (this.formInstance.validate()) {
      // 驗證通過，執行提交邏輯
      this.roleApiService.updateRole(this.role).subscribe(data => {
        this.message = data.message;
        if (data.status === "success") {
          this.router.navigate(['../index'], { relativeTo: this.route, state: { message: this.message } });
        }
      });
    }
  }

  onReset(form: any) {
    this.role = { ...this.originRole };
    form.resetForm(this.role);
    this.formInstance?.reset();
    $('#menuTree').jstree('refresh', false, true);
    this.role.items = [];
  }

  goBack(): void {
    this.router.navigate(['../index'], { relativeTo: this.route });
  }

}
