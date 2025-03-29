import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { User } from 'src/app/@models/organization.model';
import { OrganizationApiService } from 'src/app/@service/orgnization-api.service';

@Component({
  selector: 'app-organization-new-user',
  templateUrl: './organization-new-user.component.html',
  styleUrls: ['./organization-new-user.component.scss']
})
export class OrganizationNewUserComponent implements OnInit {
  @Input() nodeId!: string;
  @Output() switchComponent = new EventEmitter<{ newComponent: string, newNodeId: string | null }>; // 新增後的的新節點 code
  @Output() refreshTree = new EventEmitter<{ targetNodeId: string, clearSelected: boolean }>(); // 傳新增後的的新節點 parentCode
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

  constructor(private organizationApiService: OrganizationApiService) { }

  ngOnInit(): void {
    // 可以根據 nodeId 呼叫 API 抓資料
    this.organizationApiService.getUserForCreate(this.nodeId).subscribe(data => {
      if (data.status === "success") {
        data.user.roles = ''; // 要給roles塞值，不然html會得到null
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
      } else {
        this.message = data.message;
      }
    });
  }

  ngAfterViewInit() {
    const self = this;
    // from表單驗證
    self.formInstance = $("#newUserForm").parsley({
      errorClass: "is-invalid",
      successClass: "is-valid",
      classHandler: function (ParsleyField: any) {
        return ParsleyField.$element;
      },
      errorsWrapper: '<div class="invalid-feedback"></div>',
      errorTemplate: "<div></div>"
    });
  }

  onSubmit() {
    const usernameField = this.formInstance.fields.find((field: any) =>
      field.$element.attr('name') === 'username'
    );
    usernameField.removeError('zh-tw'); // 清除錯誤

    this.organizationApiService.checkUsernameRepeat(this.user.username).subscribe({
      next: (res: string) => {
        if (res !== 'empty') {
          // 人工設定錯誤狀態 + 訊息
          usernameField.addError('zh-tw', { message: '帳號不可重複' });
          usernameField._isValid = false; // 設為驗證失敗（非必要，但保險）
        } else {
          usernameField.removeError('zh-tw'); // 清除錯誤
          usernameField._isValid = true;

          // 進行整體 Parsley 驗證
          if (this.formInstance.validate()) {
            // 驗證通過，執行提交邏輯
            this.organizationApiService.createUser(this.user).subscribe(data => {
              this.messageChange.emit(data.message);
              this.refreshTree.emit({ targetNodeId: `user_${this.user.username}`, clearSelected: false});
              this.switchComponent.emit({ newComponent: 'editUser', newNodeId: this.user.username });
            });
          }
        }
      },
      error: () => {
        if (!usernameField._errors?.['zh-tw']) {
          usernameField.addError('zh-tw', { message: '帳號不可重複' });
        }
        usernameField.addError('zh-tw', { message: '檢查失敗，請稍後再試' });
      }
    });
  }

  onReset(form: any) {
    this.user = { ...this.originUser };
    form.resetForm(this.user);
    this.formInstance?.reset();
  }

  goBack(): void {
    // 讓父元件的 message 被清除
    this.messageChange.emit(null);
    this.switchComponent.emit({ newComponent: 'editUnit', newNodeId: null });
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
