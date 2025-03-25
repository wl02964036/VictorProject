import { animate, state, style, transition, trigger } from '@angular/animations';
import { Component, OnInit } from '@angular/core';
import { NavigationEnd, NavigationStart, Router } from '@angular/router';
import { filter } from 'rxjs';

@Component({
  selector: 'app-process-bar',
  animations: [
    trigger('startEnd', [ // 宣告動畫變數名稱為startEnd，在html使用[@startEnd]="status"即可呼叫
      state('start', style({ // 狀態設定 -> 指定key為'start'，並設定style
        width: '0%'
      })),
      state('80', style({ // 狀態設定 -> 指定key為'80'，並設定style
        width: '80%'
      })),
      state('end', style({ // 狀態設定 -> 指定key為'end'，並設定style
        width: '100%'
      })),
      transition('* => 80', [ // 轉換設定 -> 跑到80%要花10秒時間
        animate('10s')
      ]),
      transition('* => end', [ // 轉換設定 -> 跑到100%要花0.3秒時間
        animate('0.3s')
      ]),
      transition('* => start', [ // 轉換設定 -> 跑到0%要花0.0秒時間
        animate('0s')
      ]),
    ]),
  ],
  templateUrl: './process-bar.component.html',
  styleUrls: ['./process-bar.component.scss']
})
export class ProcessBarComponent implements OnInit {
  status = 'start';
  show = false;

  constructor(private router: Router) { }

  ngOnInit(): void {
    // 當事件開始時要做什麼
    this.router.events.pipe(filter(event => event instanceof NavigationStart))
      .subscribe(() => {
        this.status = 'start'; //  進度條設為0%
        this.show = true;  // 顯示進度條
        this.status = '80'; //  進度條設為80%
      });

    // 當事件結束時要做什麼
      this.router.events.pipe(filter(event => event instanceof NavigationEnd))
        .subscribe(() => {
          this.status = 'end'; //  進度條設為100%
          setTimeout(() => {
            this.show = false;  // 隱藏進度條
            this.status = 'start'; //  進度條歸0
          }, 300);
          window.scrollTo(0, 0); //  畫面置頂
        });
  }

}
