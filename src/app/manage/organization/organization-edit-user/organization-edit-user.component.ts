import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { User } from 'src/app/@models/organization.model';
import { OrganizationApiService } from 'src/app/@service/orgnization-api.service';

@Component({
  selector: 'app-organization-edit-user',
  templateUrl: './organization-edit-user.component.html',
  styleUrls: ['./organization-edit-user.component.scss']
})
export class OrganizationEditUserComponent implements OnInit, OnChanges {
  @Input() nodeId!: string;
  @Output() switchComponent = new EventEmitter<{ newComponent: string, newNodeId: string | null }>;
  @Output() refreshTree = new EventEmitter<{ targetNodeId: string }>(); // 傳被刪除的節點 parentCode
  @Input() message?: string | null = null;
  @Output() messageChange = new EventEmitter<string | null | undefined>();
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
  sexOptions = [{ id: 1, value: 'male', label: '男' }, { id: 2, value: 'female', label: '女' }, { id: 3, value: 'none', label: '不填答' }];
  enabledOptions = [{ id: 1, value: true, label: '是' }, { id: 2, value: false, label: '否' }];
  expiredOptions = [{ id: 1, value: true, label: '是' }, { id: 2, value: false, label: '否' }];
  lockedOptions = [{ id: 1, value: true, label: '是' }, { id: 2, value: false, label: '否' }];
  isInit: boolean = false; // 若ngOnInit沒觸發，就走ngOnChanges

  constructor(private organizationApiService: OrganizationApiService) { }

  ngOnInit(): void {
    this.isInit = true;
    // 可以根據 nodeId 呼叫 API 抓資料
    this.loadUser(this.nodeId);
  }
  
  ngOnChanges(changes: SimpleChanges): void {
    if (this.isInit) {
      this.loadUser(this.nodeId);
    }
  }

  ngAfterViewInit() {
    const self = this;
    // from表單驗證
    self.formInstance = $("#editUserForm").parsley({
      errorClass: "is-invalid",
      successClass: "is-valid",
      classHandler: function (ParsleyField: any) {
        return ParsleyField.$element;
      },
      errorsWrapper: '<div class="invalid-feedback"></div>',
      errorTemplate: "<div></div>"
    });
  }

  loadUser(nodeId: string): void {
    this.organizationApiService.getUserForUpdate(nodeId).subscribe(data => {
      if (data.status === "success") {
        this.originUser = { ...data.user };
        this.user = { ...data.user };
        this.roleTuples = [...data.roleTuples];
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

  onSubmit() {
    if (this.formInstance.validate()) {
      // 驗證通過，執行提交邏輯
      this.organizationApiService.updateUser(this.user).subscribe(data => {
        this.messageChange.emit(data.message);
        this.refreshTree.emit({ targetNodeId: `user_${this.user.username}` });
      });
    }
  }

  onReset(form: any): void {
    this.user = { ...this.originUser };
    form.resetForm(this.user);
    this.formInstance?.reset();
  }

  onDestroy(): void {
    if (confirm('確定要刪除此人員？')) {
      this.organizationApiService.deleteUser(this.user.username, this.user.unitCode).subscribe(data => {
        this.messageChange.emit(data.message);
        this.refreshTree.emit({ targetNodeId: `unit_${this.user.unitCode}` });
        this.switchComponent.emit({ newComponent: 'editUnit', newNodeId: this.user.unitCode });
      });
    }
  }

  onChangePassword(): void {
    // 讓父元件的 message 被清除
    this.messageChange.emit(null);
    this.switchComponent.emit({ newComponent: 'editPassword', newNodeId: null });
  }

  onUnlock(): void {
    this.organizationApiService.unlockErrorPassword(this.user.username).subscribe(data => {
      if (data === "success") {
        alert("解除鎖定成功");
      } else if (data === "failure") {
          alert("解除鎖定失敗");
      } else {
          alert(data);
      }
    });
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
