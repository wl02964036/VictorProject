package com.rx.core.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SystemRole extends Auditable implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;
	private String title;
	private String description;
	private Boolean assignable; // 機關管理者是否可以指定此角色
}
