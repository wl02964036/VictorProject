export interface LoginPost {
  username: string,
  password: string,
  captcha: string
}

export interface LoginResponse {
    status: string;
    message?: string;
    token: string;
    systemUser: SystemUser;
    sidebarMenus: SidebarMenu;
}

export interface SystemUser {
  username: string,
  displayName: string,
  sex: string,
  email: string,
  tel: string,
  enabled: boolean,
  expired: boolean,
  locked: boolean,
  unitCode: string,
  pwdUpdateAt: Date,
  loginErrors: number,
  lockedAt: Date,
  unit: SystemUnit
}

export interface SystemUnit {
  code: string,
  displayName: string,
  fax: string,
  email: string,
  tel: string,
  parent: string,
  path: string,
  weight: number,
  createdAt: Date,
  createdBy: string,
  updatedAt: Date,
  updatedBy: string
}

export interface SidebarMenu {
  uuid: string,
  path: string,
  title: string,
  children: SidebarMenu[]
}