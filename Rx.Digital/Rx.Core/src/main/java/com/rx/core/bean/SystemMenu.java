package com.rx.core.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class SystemMenu implements Serializable {

	private static final long serialVersionUID = 1L;

	private String uuid;
	private Integer level;
	private String path;
	private String title;
	private Long weight;
	private String parent;
}
