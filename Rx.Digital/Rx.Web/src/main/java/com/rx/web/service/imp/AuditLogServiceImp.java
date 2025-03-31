package com.rx.web.service.imp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.ModelMap;

import com.rx.core.bean.AuditLog;
import com.rx.core.bean.SystemUser;
import com.rx.core.dao.AuditLogDao;
import com.rx.core.support.ClientIPSupport;
import com.rx.core.table.request.AuditLogTableRequest;
import com.rx.web.service.AuditLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuditLogServiceImp implements AuditLogService {

	@Autowired
	protected AuditLogDao auditLogDao;

	@SuppressWarnings("unchecked")
	@Transactional
	public void create(HttpServletRequest request, ModelMap model, String type, String target, String subject,
			String mark) throws Exception {
		String ip = ClientIPSupport.getIPFromXFHeader(request);
		LocalDateTime current = LocalDateTime.now();
		SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
		Tuple3<String, String, String> pathTuple = (Tuple3<String, String, String>) model
				.getAttribute("currentPathTuple");

		AuditLog auditLog = new AuditLog();
		if (loginUser != null) {
			auditLog.setUsername(loginUser.getUsername());
		} else {
			auditLog.setUsername("");
		}
		auditLog.setActionedAt(current);
		auditLog.setIp(ip);

		if (pathTuple != null) {
			auditLog.setUuid(pathTuple.v1);
			auditLog.setTitle(pathTuple.v3);
		} else {
			auditLog.setUuid("");
			auditLog.setTitle("");
		}

		auditLog.setType(type);
		auditLog.setTarget(target);
		auditLog.setSubject(subject);
		auditLog.setMark(mark);
		Long id = auditLogDao.create(auditLog);
	}

	@Transactional(readOnly = true)
	public Tuple2<Long, List<AuditLog>> query(final AuditLogTableRequest dataTableRequest) throws Exception {
		Long totalCount = auditLogDao.totalCount(dataTableRequest);
		List<AuditLog> pageContent = auditLogDao.queryByPage(dataTableRequest);
		pageContent = pageContent.stream().map(auditLog -> {
			auditLog.setTitle(auditLog.getTitle() + " : '" + auditLog.getSubject() + "'");
			return auditLog;
		}).collect(Collectors.toList());

		return Tuple.tuple(totalCount, pageContent);
	}
}
