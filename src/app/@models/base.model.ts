import { map } from 'rxjs';
export interface DataTableResponse<T = any> {
    draw: number;
    recordsTotal: number;
    recordsFiltered: number;
    data: T[];
    error: string;
}

export interface BaseResponse {
    status: string;
    message?: string;
}

export interface TreeNodeModel {
    id: string;
    text: string;
    icon: string;
    data: string;
    state: Map<string, boolean>;
    li_attr: Map<string, string>;
    a_attr: Map<string, string>;
    children: object;
}
