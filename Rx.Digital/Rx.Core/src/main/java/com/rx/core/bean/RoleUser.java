package com.rx.core.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class RoleUser implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;
	private String code;
	private String username;
}
