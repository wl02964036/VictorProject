package com.rx.web.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.tuple.Tuple2;

import com.rx.core.bean.LoginLog;
import com.rx.core.table.request.LoginLogTableRequest;

public interface LoginLogService {

	public void create(HttpServletRequest request, String username, String status, String cause) throws Exception;

	public Tuple2<Long, List<LoginLog>> query(final LoginLogTableRequest dataTableRequest) throws Exception;
}
