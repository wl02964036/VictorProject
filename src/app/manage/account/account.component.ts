import { RoleApiService } from './../../@service/role-api.service';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { Role } from 'src/app/@models/role.model';

@Component({
  selector: 'app-account',
  templateUrl: './account.component.html',
  styleUrls: ['./account.component.scss']
})
export class AccountComponent implements OnInit {
  roleForm!: FormGroup;
  roleData!: Role;

  constructor(private fb: FormBuilder, private route: ActivatedRoute, private roleApiService: RoleApiService) {

    // 預先載入資料Resolve
    this.route.data.subscribe({
      next: (data) => {
        this.roleData = data["roleData"];
      },
      error: (err) => {
        alert(`${err.message}`);
      }
    });

    this.roleForm = this.fb.group({
      code: [{ value: this.roleData.code, disabled: true }],
      title: [this.roleData.title, [Validators.required, Validators.maxLength(256)]],
      description: [this.roleData.description, [Validators.maxLength(512)]],
      assignable: [this.roleData.assignable, Validators.required]
    });
  }
  
  ngOnInit(): void {
    throw new Error('Method not implemented.');
  }

  onSubmit(): void {
    if (this.roleForm.invalid) {
      // 強制讓所有表單欄位都標示為「已碰觸」 ➜ Angular 通常只有在欄位被碰觸過後才會顯示驗證錯誤訊息
      this.roleForm.markAllAsTouched();
      return;
    }
    const formValue: Role = this.roleForm.getRawValue();
    console.log('Submit:', formValue);
    this.roleApiService.updateRole(formValue).subscribe(data => {
      if (data.status !== "success") {
        alert(data.message);
      }
    });
    // TODO: handle tree items and final submission
  }

  onReset(): void {
    this.roleForm.reset({
      code: { value: this.roleData.code, disabled: true },
      title: this.roleData.title,
      description: this.roleData.description,
      assignable: this.roleData.assignable
    });
  }

  onBack(): void {
    // TODO: implement navigation logic
    console.log('Back to list');
  }

}
