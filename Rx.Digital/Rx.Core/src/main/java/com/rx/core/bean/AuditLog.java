package com.rx.core.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class AuditLog implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String TYPE_VIEW = "view";
	public static String TYPE_CREATE = "create";
	public static String TYPE_UPDATE = "update";
	public static String TYPE_DELETE = "delete";

	private Long id;
	private String username; // 作動者帳號

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime actionedAt; // 作動時間

	private String ip; // 作動者IP
	private String title; // 選單名稱
	private String uuid; // 選單uuid
	private String type; // 動作
	private String target; // 作動目標 (請存 該項資料的 primary key)
	private String subject; // 作動目標 顯示名稱
	private String mark; // 其他註記

}
