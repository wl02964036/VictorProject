import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { PasswordModel } from 'src/app/@models/organization.model';
import { OrganizationApiService } from 'src/app/@service/orgnization-api.service';

@Component({
  selector: 'app-organization-edit-password',
  templateUrl: './organization-edit-password.component.html',
  styleUrls: ['./organization-edit-password.component.scss']
})
export class OrganizationEditPasswordComponent implements OnInit {
  @Input() nodeId!: string;
  @Output() switchComponent = new EventEmitter<{ newComponent: string, newNodeId: string | null }>;
  @Input() message?: string | null = null;
  @Output() messageChange = new EventEmitter<string | null | undefined>();
  formInstance: any;
  originPasswordModel!: PasswordModel;
  passwordModel: PasswordModel = {
    username: '',
    displayName: '',
    code: '',
    confirmCode: ''
  };

  constructor(private organizationApiService: OrganizationApiService) { }

  ngOnInit(): void {
    // 可以根據 nodeId 呼叫 API 抓資料
    this.loadUser(this.nodeId);
  }

  ngAfterViewInit() {
    const self = this;
    // from表單驗證
    self.formInstance = $("#editPasswordForm").parsley({
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
    this.organizationApiService.getPasswordForUpdate(nodeId).subscribe(data => {
      if (data.status === "success") {
        this.originPasswordModel = { ...data.passwordModel };
        this.passwordModel = { ...data.passwordModel };

        // 若已初始化過 parsley，要重置
        if (this.formInstance) {
          this.formInstance.reset();
        }

        // 清除錯誤訊息
        this.message = null;
      } else {
        this.message = data.message;
      }
    });
  }

  onSubmit() {
    if (this.formInstance.validate()) {
      // 驗證通過，執行提交邏輯
      this.organizationApiService.updatePassword(this.passwordModel).subscribe(data => {
        this.messageChange.emit(data.message);
        this.switchComponent.emit({ newComponent: 'editUser', newNodeId: null });
      });
    }
  }

  onReset(form: any): void {
    this.passwordModel = { ...this.originPasswordModel };
    form.resetForm(this.passwordModel);
    this.formInstance?.reset();
  }

  goBack(): void {
    // 讓父元件的 message 被清除
    this.messageChange.emit(null);
    this.switchComponent.emit({ newComponent: 'editUser', newNodeId: null });
  }

}
