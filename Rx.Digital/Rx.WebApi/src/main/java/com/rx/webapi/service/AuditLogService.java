package com.rx.webapi.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.tuple.Tuple2;

import com.rx.core.bean.AuditLog;
import com.rx.core.bean.SystemUser;
import com.rx.core.table.request.AuditLogTableRequest;

public interface AuditLogService {
	public void create(HttpServletRequest request, SystemUser loginUser, String type, String target, String subject,
			String mark) throws Exception;

	public Tuple2<Long, List<AuditLog>> query(final AuditLogTableRequest dataTableRequest) throws Exception;
}
