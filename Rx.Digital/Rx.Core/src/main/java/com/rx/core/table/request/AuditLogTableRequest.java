package com.rx.core.table.request;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import com.rx.core.support.datatable.DataTableRequest;

import lombok.Data;

@Data
public class AuditLogTableRequest extends DataTableRequest {

	private static final long serialVersionUID = 1L;

	private String queryUsername;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate queryActionedAt;

}
