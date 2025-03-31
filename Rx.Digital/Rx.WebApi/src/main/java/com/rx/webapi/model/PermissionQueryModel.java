package com.rx.webapi.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class PermissionQueryModel implements Serializable {
	private static final long serialVersionUID = 1L;
	private String unitName;
	private String displayName;
	private String username;
	private String roles;
}
