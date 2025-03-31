package com.rx.core.bean;

import java.io.Serializable;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SystemUnit extends Auditable implements Serializable {

	private static final long serialVersionUID = 1L;

	private String code;
	private String displayName;
	private String fax;
	private String email;
	private String tel;
	private String parent;
	private String path;
	private Long weight;

}
