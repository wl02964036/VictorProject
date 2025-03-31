package com.rx.core.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TodoD implements Serializable {

	private static final long serialVersionUID = 1L;

	private String groupId;
	private String todoId;
	private String name;
	private Boolean value;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime createAt; // 作動時間

	private String createBy; // 作動者帳號

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updateAt; // 作動時間

	private String updateBy; // 作動者帳號

	// Constructor
	public TodoD(String groupId, String todoId, String name, Boolean value, LocalDateTime actionedAt, String username) {
		this.groupId = groupId;
		this.todoId = todoId;
		this.name = name;
		this.value = value;
		this.createAt = actionedAt;
		this.createBy = username;
		this.updateAt = actionedAt;
		this.updateBy = username;
	}

	public TodoD(String groupId, String name, Boolean value, LocalDateTime actionedAt, String username) {
		this.groupId = groupId;
		this.todoId = "";
		this.name = name;
		this.value = value;
		this.createAt = actionedAt;
		this.createBy = username;
		this.updateAt = actionedAt;
		this.updateBy = username;
	}
}
