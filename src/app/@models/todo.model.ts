export interface Todo {
    todoId: string;
    name: string;
    value: boolean;
    editing: boolean;
    canEdit: boolean;
    seqno: number;
    createAt: Date;
}

export class TodoClass implements Todo {
    todoId: string;
    name: string;
    value: boolean;
    editing: boolean;
    canEdit: boolean;
    seqno: number;
    createAt: Date;

    constructor(_name: string, _seqno: number, _value: boolean = false, _editing: boolean = false, _canEdit: boolean = false) {
        this.todoId = "", 
        this.name = _name;
        this.value = _value;
        this.editing = _editing;
        this.canEdit = _canEdit;
        this.seqno = _seqno;
        this.createAt =  new Date();
    }

    toggle() {
        this.value = !this.value;
    }
}

export enum TodoStatusType {
    All,
    Active,
    Completed
}

export interface TodoResponse {
    status: string;
    message?: string;
    todoId: string;
}