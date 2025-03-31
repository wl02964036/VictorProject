package com.rx.web.modal;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserModel implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;
    private String code;
    private String confirmCode;
    private String displayName;
    private String sex;
    private String email;
    private String tel;
    private Boolean enabled;
    private Boolean expired;
    private Boolean locked;
    private String unitCode;
    private String roles;  // 角色 (checkbox 多選，以逗號分隔)

}
