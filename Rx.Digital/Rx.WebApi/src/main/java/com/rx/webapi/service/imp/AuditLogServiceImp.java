package com.rx.webapi.service.imp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.AntPathMatcher;

import com.rx.core.bean.AuditLog;
import com.rx.core.bean.SystemUser;
import com.rx.core.dao.AuditLogDao;
import com.rx.core.support.ClientIPSupport;
import com.rx.core.table.request.AuditLogTableRequest;
import com.rx.webapi.service.AuditLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class AuditLogServiceImp implements AuditLogService {

	@Autowired
	protected AuditLogDao auditLogDao;

	@Autowired
	protected List<Tuple3<String, String, String>> menuPathTuples;

	private static final AntPathMatcher pathMatcher = new AntPathMatcher();

	@SuppressWarnings("unchecked")
	@Transactional(rollbackFor = Exception.class)
	public void create(HttpServletRequest request, SystemUser loginUser, String type, String target, String subject,
			String mark) throws Exception {
		String ip = ClientIPSupport.getIPFromXFHeader(request);
		LocalDateTime current = LocalDateTime.now();

		// requestURI = contextPath + servletPath + pathInfo
		String servletPath = getCleanServletPath(request);

		Optional<Tuple3<String, String, String>> targetOpt = menuPathTuples.stream().filter(tuple3 -> {
			return pathMatcher.match(tuple3.v2, servletPath);
		}).findAny();

		Tuple3<String, String, String> pathTuple = targetOpt.isPresent() ? targetOpt.get() : null;

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

	private String getCleanServletPath(HttpServletRequest request) {
		// requestURI = contextPath + servletPath + pathInfo
		String servletPath = request.getServletPath();

		if (servletPath.startsWith("/angular")) {
			servletPath = servletPath.replace("/angular", "");
		}
		return servletPath;
	}
}
