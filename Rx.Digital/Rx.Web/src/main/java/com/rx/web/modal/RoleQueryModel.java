package com.rx.web.modal;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class RoleQueryModel implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private String code;
	private String title;
	private String updatedBy;
	private String updateUnit;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private LocalDateTime updatedAt;
}
