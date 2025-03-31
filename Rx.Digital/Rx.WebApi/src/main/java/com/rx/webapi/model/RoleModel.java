package com.rx.webapi.model;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

@Data
public class RoleModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code; // 單位代碼
	private String title;
	private String description;
	private Boolean assignable = false;
	private List<String> items;

}
