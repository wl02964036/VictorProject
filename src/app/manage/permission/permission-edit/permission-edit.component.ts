import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from 'src/app/@models/organization.model';
import { PermissionApiService } from 'src/app/@service/permission-api.service';

@Component({
  selector: 'app-permission-edit',
  templateUrl: './permission-edit.component.html',
  styleUrls: ['./permission-edit.component.scss']
})
export class PermissionEditComponent implements OnInit {
  message?: string | null = null;
  username: string = '';
  unitName: string = '';
  formInstance: any;
  originUser!: User;
  user: User = {
    username: '',
    code: '',
    confirmCode: '',
    displayName: '',
    sex: 'none',
    email: '',
    tel: '',
    enabled: false,
    expired: false,
    locked: false,
    unitCode: '',
    roles: ''
  };
  roleTuples: { v1: string; v2: string }[] = [];
  classHandler: string = '';

  constructor(private permissionApiService: PermissionApiService,
    private router: Router,
    private route: ActivatedRoute) { }

  ngOnInit(): void {
    const params = new URLSearchParams(window.location.search);
    if (params.has('username')) {
      this.username = params.get('username') as string;
    }

    this.permissionApiService.getPermissionForUpdate(this.username).subscribe(data => {
      if (data.status === "success") {
        this.originUser = { ...data.user };
        this.user = { ...data.user };
        this.roleTuples = [...data.roleTuples];
        this.unitName = data.unitName;
        for (let index = 0; index < this.roleTuples.length; index++) {
          if (index === 0) {
            this.classHandler = `#roles${index}`;
          }else {
            this.classHandler = this.classHandler + `,#roles${index}`
          }
        }

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
    this.initForm();
  }

  initForm() {
    const self = this;
    // from表單驗證
    self.formInstance = $("#userForm").parsley({
      errorClass: "is-invalid",
      successClass: "is-valid",
      classHandler: function (ParsleyField: any) {
        return ParsleyField.$element;
      },
      errorsWrapper: '<div class="invalid-feedback"></div>',
      errorTemplate: "<div></div>"
    });
  }

  onSubmit(): void {
    // 進行整體 Parsley 驗證
    if (this.formInstance.validate()) {
      // 驗證通過，執行提交邏輯
      this.permissionApiService.updatePermission(this.user).subscribe(data => {
        this.message = data.message;
        if (data.status === "success") {
          this.router.navigate(['../index'], { relativeTo: this.route, state: { message: this.message } });
        }
      });
    }
  }

  onReset(form: any) {
    this.user = { ...this.originUser };
    form.resetForm(this.user);
    this.formInstance?.reset();
  }

  goBack(): void {
    this.router.navigate(['../index'], { relativeTo: this.route });
  }

  onRoleChange(event: Event, role: string): void {
    const checked = (event.target as HTMLInputElement).checked;

    // 將字串分割為陣列
    let rolesArray = this.user.roles ? this.user.roles.split(',') : [];

    if (checked) {
      if (!rolesArray.includes(role)) {
        rolesArray.push(role);
      }
    } else {
      rolesArray = rolesArray.filter((r: string) => r !== role);
    }
  
    // 再轉回字串存回原本的屬性
    this.user.roles = rolesArray.join(',');
  }
}
