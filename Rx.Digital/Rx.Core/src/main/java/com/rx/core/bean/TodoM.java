package com.rx.core.bean;

import java.io.Serializable;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TodoM implements Serializable {

	private static final long serialVersionUID = 1L;

	private String groupId;
	private String name;

	// Constructor
	public TodoM(String groupId, String name) {
		this.groupId = groupId;
		this.name = name;
	}
}
