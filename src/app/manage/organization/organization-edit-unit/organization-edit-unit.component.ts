import { Component, EventEmitter, Input, OnChanges, OnInit, Output, SimpleChanges } from '@angular/core';
import { Unit } from 'src/app/@models/organization.model';
import { OrganizationApiService } from 'src/app/@service/orgnization-api.service';

@Component({
  selector: 'app-organization-edit-unit',
  templateUrl: './organization-edit-unit.component.html',
  styleUrls: ['./organization-edit-unit.component.scss']
})
export class OrganizationEditUnitComponent implements OnInit, OnChanges {
  @Input() nodeId!: string;
  @Output() switchComponent = new EventEmitter<{ newComponent: string, newNodeId: string | null }>;
  @Output() refreshTree = new EventEmitter<{ targetNodeId: string }>(); // 傳被刪除的節點 parentCode
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
    parentCode: ''
  };
  isInit: boolean = false; // 若ngOnInit沒觸發，就走ngOnChanges

  constructor(private organizationApiService: OrganizationApiService) { }

  ngOnInit(): void {
    this.isInit = true;
    // 可以根據 nodeId 呼叫 API 抓資料
    this.loadUnit(this.nodeId);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.isInit) {
      this.loadUnit(this.nodeId);
    }
  }

  ngAfterViewInit() {
    const self = this;
    // from表單驗證
    self.formInstance = $("#editUnitForm").parsley({
      errorClass: "is-invalid",
      successClass: "is-valid",
      classHandler: function (ParsleyField: any) {
        return ParsleyField.$element;
      },
      errorsWrapper: '<div class="invalid-feedback"></div>',
      errorTemplate: "<div></div>"
    });
  }

  loadUnit(nodeId: string): void {
    this.organizationApiService.getUnitForUpdate(nodeId).subscribe(data => {
      if (data.status === "success") {
        this.originUnit = { ...data.unit };
        this.unit = { ...data.unit };

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
      this.organizationApiService.updateUnit(this.unit).subscribe(data => {
        this.message = data.message;
        this.refreshTree.emit({ targetNodeId: `unit_${this.unit.code}` });
      });
    }
  }

  onReset(form: any) {
    this.unit = { ...this.originUnit };
    form.resetForm(this.unit);
    this.formInstance?.reset();
  }

  onDestroy() {
    if (confirm("確定要刪除此單位？")) {
      this.organizationApiService.deleteUnit(this.unit.code).subscribe(data => {
        this.message = data.message;
        this.refreshTree.emit({ targetNodeId: `unit_${this.unit.parentCode}` });
        this.switchComponent.emit({ newComponent: 'editUnit', newNodeId: this.unit.parentCode });
      });
    }
  }

  onNewUnit() {
    // 讓父元件的 message 被清除
    this.messageChange.emit(null);
    this.switchComponent.emit({ newComponent: 'newUnit', newNodeId: null });
  }

  onNewUser() {
    // 讓父元件的 message 被清除
    this.messageChange.emit(null);
    this.switchComponent.emit({ newComponent: 'newUser', newNodeId: null });
  }

}
