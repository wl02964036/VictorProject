import { OrganizationApiService } from 'src/app/@service/orgnization-api.service';
import { Component, OnInit } from '@angular/core';
import { AssetLoaderService } from 'src/app/@service/asset-loader.service';

@Component({
  selector: 'app-organization-index',
  templateUrl: './organization-index.component.html',
  styleUrls: ['./organization-index.component.scss']
})
export class OrganizationIndexComponent implements OnInit {
  message?: string | null = null;
  targetId: string | null = null;
  currentComponent: string | null = null;
  selectedNodeId: string = '';

  constructor(private assetLoader: AssetLoaderService, private organizationApiService: OrganizationApiService) { }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    // 載入 css & js 後，再執行init操作
    this.assetLoader.loadAssets(
      [
        'assets/sb-admin/vendor/jstree/jstree.min.js',
        'assets/sb-admin/vendor/iframe-resizer/iframeResizer.min.js',
        'assets/sb-admin/vendor/application/customValidator.js'
      ],
      [
        'assets/sb-admin/vendor/jstree/themes/default/style.min.css'
      ]
    ).then(() => {
      this.initJsTree();
      this.resizeIframe();
    });
  }

  onSwitchComponent(nextComponent: string, newNodeId: string | null) {
    this.currentComponent = nextComponent;
    if (newNodeId) {
      this.selectedNodeId = newNodeId;
    }
  }

  exportExcel() {
    this.organizationApiService.downloadReport().subscribe(blob => {
      const a = document.createElement('a');
      const objectUrl = URL.createObjectURL(blob);
      a.href = objectUrl;
      a.download = '人員組織報表.xls'; // 👈 可根據實際檔名動態指定
      a.click();
      URL.revokeObjectURL(objectUrl);
    });
  }

  onRefreshTree(targetNodeId: string): void {
    this.targetId = targetNodeId;
    $("#organizationTree").jstree(true).refresh();
  }

  initJsTree() {
    const self = this;
    $("#organizationTree")
      .jstree({
        core: {
          data: function (node: any, cb: any) {
            self.organizationApiService.getMenuTree(node.id).subscribe({
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
            dots: true,
            stripes: false,
            responsive: false,
            variant: "large",
          },
        },
      })
      .on("refresh.jstree", function (e: any, data: any) {
        if (data.instance._data.core.selected) {
          if (data.instance._data.core.selected.length > 0) {
            console.log("open >> " + data.instance._data.core.selected[0]);
            $("#organizationTree").jstree(true).open_node(data.instance._data.core.selected[0]);
          }
        }

        if (self.targetId != null) {
          $("#organizationTree").jstree(true).deselect_all(true);
          $("#organizationTree").jstree(true).select_node(self.targetId, true, true);
          self.targetId = null;
        }
        return false;
      })
      .on("select_node.jstree", function (e: any, data: any) {
        var node = data.node;

        // 檢查是不是 refresh 過
        if (self.targetId) {
          if (self.targetId != node.id) {
            const targetArr = self.targetId.split('_');
            if (targetArr[0] === "unit") {
              self.currentComponent = 'editUnit';
              self.selectedNodeId = self.targetId.replace("unit_", "");
            } else if (targetArr[0] === "user") {
              self.currentComponent = 'editUser';
              self.selectedNodeId = self.targetId.replace("user_", "");
            } else {
              self.currentComponent = null;
              self.selectedNodeId = '';
            }
          } else {
            // 不重新載入 iframe
          }
        } else {
          // 到這裡一般都是直接點JsTree
          self.message = null;
          if (node.data === "unit") {
            self.currentComponent = 'editUnit';
            self.selectedNodeId = node.id.replace("unit_", "");
          } else if (node.data === "user") {
            self.currentComponent = 'editUser';
            self.selectedNodeId = node.id.replace("user_", "");
          } else {
            self.currentComponent = null;
            self.selectedNodeId = '';
          }
        }
      });
  }

  resizeIframe(): void {
    const isOldIE = navigator.userAgent.indexOf('MSIE') !== -1;
    $('#mainframe').iFrameResize({
      heightCalculationMethod: isOldIE ? 'max' : 'lowestElement',
      checkOrigin: false
    });
  }

}
