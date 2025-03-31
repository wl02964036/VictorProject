package com.rx.core.bean;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class LoginLog implements Serializable {

	private static final long serialVersionUID = 1L;

	public static String STATUS_SUCCEED = "succeed";
	public static String STATUS_FAILED = "failed";

	private Long id;
	private String username; // 登入者帳號

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime actionedAt;

	private String ip;
	private String status; // 狀態 'succeed' 或 'failed'
	private String cause; // 失敗的Exception
}
