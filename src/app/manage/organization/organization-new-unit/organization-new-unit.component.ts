import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { Unit } from 'src/app/@models/organization.model';
import { OrganizationApiService } from 'src/app/@service/orgnization-api.service';

@Component({
  selector: 'app-organization-new-unit',
  templateUrl: './organization-new-unit.component.html',
  styleUrls: ['./organization-new-unit.component.scss']
})
export class OrganizationNewUnitComponent implements OnInit {
  @Input() nodeId!: string;
  @Output() switchComponent = new EventEmitter<{ newComponent: string, newNodeId: string | null }>; // 新增後的的新節點 code
  @Output() refreshTree = new EventEmitter<{ targetNodeId: string, clearSelected: boolean }>(); // 傳新增後的的新節點 parentCode
  @Input() message?: string | null = null;
  @Output() messageChange = new EventEmitter<string | null | undefined>();
  formInstance: any;
  originUnit!: Unit;
  unit: Unit = {
    code: '',
    displayName: '',
    fax: '',
    email: '',
    tel: '',
    weight: 0,
    parentCode: this.nodeId
  };

  constructor(private organizationApiService: OrganizationApiService) { }

  ngOnInit(): void {
    // 可以根據 nodeId 呼叫 API 抓資料
    this.organizationApiService.getUnitForCreate(this.nodeId).subscribe(data => {
      if (data.status === "success") {
        this.originUnit = { ...data.unit };
        this.unit = { ...data.unit };
      } else {
        this.message = data.message;
      }
    });
  }

  ngAfterViewInit() {
    const self = this;

    // from表單驗證
    self.formInstance = $("#newUnitForm").parsley({
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
    const codeField = this.formInstance.fields.find((field: any) =>
      field.$element.attr('name') === 'code'
    );
    codeField.removeError('zh-tw'); // 清除錯誤

    this.organizationApiService.checkUnitCodeRepeat(this.unit.code).subscribe({
      next: (res: string) => {
        if (res !== 'empty') {
          // 人工設定錯誤狀態 + 訊息
          codeField.addError('zh-tw', { message: '代碼不可重複' });
          codeField._isValid = false; // 設為驗證失敗（非必要，但保險）
        } else {
          codeField.removeError('zh-tw'); // 清除錯誤
          codeField._isValid = true;

          // 進行整體 Parsley 驗證
          if (this.formInstance.validate()) {
            // 驗證通過，執行提交邏輯
            this.organizationApiService.createUnit(this.unit).subscribe(data => {
              this.messageChange.emit(data.message);
              this.refreshTree.emit({ targetNodeId: `unit_${this.unit.code}`, clearSelected: false});
              this.switchComponent.emit({ newComponent: 'editUnit', newNodeId: this.unit.code });
            });
          }
        }
      },
      error: () => {
        if (codeField._errors?.['zh-tw']) {
          codeField.removeError('zh-tw'); // 清除錯誤
        }
        codeField.addError('zh-tw', { message: '檢查失敗，請稍後再試' });
      }
    });
  }

  onReset(form: any) {
    this.unit = { ...this.originUnit };
    form.resetForm(this.unit);
    this.formInstance?.reset();
  }

  goBack(): void {
    // 讓父元件的 message 被清除
    this.messageChange.emit(null);
    this.switchComponent.emit({ newComponent: 'editUnit', newNodeId: null });
  }

}
