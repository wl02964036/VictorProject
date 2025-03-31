package com.rx.web.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.tuple.Tuple2;
import org.springframework.ui.ModelMap;

import com.rx.core.bean.AuditLog;
import com.rx.core.table.request.AuditLogTableRequest;

public interface AuditLogService {
	public void create(HttpServletRequest request, ModelMap model, String type, String target, String subject,
			String mark) throws Exception;

	public Tuple2<Long, List<AuditLog>> query(final AuditLogTableRequest dataTableRequest) throws Exception;
}
