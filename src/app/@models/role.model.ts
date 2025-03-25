export interface Role {
    code: string;
    title: string;
    description: string;
    assignable: boolean;
    items: string[];
}

export interface RoleResponse {
    status: string;
    message?: string;
    roleBind: Role;
}
