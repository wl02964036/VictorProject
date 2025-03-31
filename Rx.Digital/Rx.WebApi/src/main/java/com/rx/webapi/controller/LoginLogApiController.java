package com.rx.webapi.controller;

import java.util.List;

import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rx.core.bean.LoginLog;
import com.rx.core.support.datatable.DataTableResponse;
import com.rx.core.table.request.LoginLogTableRequest;
import com.rx.webapi.service.LoginLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/angular")
public class LoginLogApiController {

	@Autowired
	protected LoginLogService loginLogService;

	@PostMapping(name = "login-log.query", path = "/login-log/query", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DataTableResponse query(final LoginLogTableRequest dataTableRequest) {
		try {
			Tuple2<Long, List<LoginLog>> queryTuple = loginLogService.query(dataTableRequest);
			DataTableResponse tableResponse = new DataTableResponse();
			tableResponse.setDraw(dataTableRequest.getDraw());
			tableResponse.setRecordsTotal(queryTuple.v1);
			tableResponse.setRecordsFiltered(queryTuple.v1);
			tableResponse.setData(queryTuple.v2);
			return tableResponse;
		} catch (Exception e) {
			log.error("", e);
			DataTableResponse tableResponse = new DataTableResponse();
			tableResponse.setDraw(dataTableRequest.getDraw());
			tableResponse.setError("Error : " + e.getMessage());
			return tableResponse;
		}
	}

}
