import { User } from "./organization.model";


export interface GetPermissionResponse {
    status: string;
    message?: string;
    user: User;
    roleTuples: { v1: string; v2: string }[];
    unitName: string;
}