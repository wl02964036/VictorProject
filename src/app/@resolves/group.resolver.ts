import { Injectable } from '@angular/core';
import {
  Resolve,
  RouterStateSnapshot,
  ActivatedRouteSnapshot
} from '@angular/router';
import { Observable, of } from 'rxjs';
import { GroupApiService } from '../@service/group-api.service';
import { Group } from '../@models/group.model';

@Injectable({
  providedIn: 'root'
})
export class GroupResolver implements Resolve<Group[]> {

  constructor(private todoApiService: GroupApiService) { }

  resolve(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<Group[]> {
    return this.todoApiService.getGroupList();
  }
}
