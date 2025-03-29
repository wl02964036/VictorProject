export interface Unit {
    code: string;
    displayName: string;
    fax: string;
    email: string;
    tel: string;
    weight: number;
    parentCode: string;
}

export interface GetUnitResponse {
    status: string;
    message?: string;
    unit: Unit;
}

export interface SaveUnitResponse {
    status: string;
    message?: string;
}

export interface User {
    username: string;
    code: string;
    confirmCode: string;
    displayName: string;
    sex: string;
    email: string;
    tel: string;
    enabled: boolean;
    expired: boolean;
    locked: boolean;
    unitCode: string;
    roles: string;  // 角色 (checkbox 多選，以逗號分隔)
}

export interface GetUserResponse {
    status: string;
    message?: string;
    user: User;
    roleTuples: { v1: string; v2: string }[];
}

export interface SaveUserResponse {
    status: string;
    message?: string;
}

export interface PasswordModel {
    username: string;
    displayName: string;
    code: string;
    confirmCode: string;
}

export interface GetPasswordResponse {
    status: string;
    message?: string;
    passwordModel: PasswordModel;
}

export interface SavePasswordResponse {
    status: string;
    message?: string;
}
