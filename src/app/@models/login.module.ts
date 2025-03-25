export interface LoginPost {
  username: string,
  pwd: string
}

export interface LoginResponse {
    status: string;
    message?: string;
    token: string;
}