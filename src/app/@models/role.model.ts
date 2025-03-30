export interface Role {
    code: string;
    title: string;
    description: string;
    assignable: boolean;
    items: string[];
}

export interface RoleQuery {
    code: string;
    title: string;
    updatedBy: string;
    updateUnit: string;
    updatedAt: Date;
}

export interface RoleResponse {
    status: string;
    message?: string;
    role: Role;
}
