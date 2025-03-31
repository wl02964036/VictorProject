package com.rx.webapi.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class UnitModel implements Serializable {
	private static final long serialVersionUID = 1L;

	private String code; // 單位代碼
	private String displayName;
	private String fax;
	private String email;
	private String tel;
	private Long weight;
	private String parentCode;

}
