package com.rx.webapi.service.imp;

import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.rx.core.bean.LoginLog;
import com.rx.core.dao.LoginLogDao;
import com.rx.core.support.ClientIPSupport;
import com.rx.core.table.request.LoginLogTableRequest;
import com.rx.webapi.service.LoginLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginLogServiceImp implements LoginLogService {

	@Autowired
	protected LoginLogDao loginLogDao;

	@Transactional
	public void create(HttpServletRequest request, String username, String status, String cause) throws Exception {
		String ip = ClientIPSupport.getIPFromXFHeader(request);
		LocalDateTime current = LocalDateTime.now();

		LoginLog loginLog = new LoginLog();
		loginLog.setUsername(username);
		loginLog.setActionedAt(current);
		loginLog.setIp(ip);
		loginLog.setStatus(status);
		loginLog.setCause(cause);

		Long id = loginLogDao.create(loginLog);
	}

	@Transactional(readOnly = true)
	public Tuple2<Long, List<LoginLog>> query(final LoginLogTableRequest dataTableRequest) throws Exception {
		Long totalCount = loginLogDao.totalCount(dataTableRequest);
		List<LoginLog> pageContent = loginLogDao.queryByPage(dataTableRequest);
		return Tuple.tuple(totalCount, pageContent);
	}
}
