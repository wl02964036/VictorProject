package com.rx.web.controller;

import java.util.List;

import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rx.core.bean.AuditLog;
import com.rx.core.support.datatable.DataTableResponse;
import com.rx.core.table.request.AuditLogTableRequest;
import com.rx.web.service.AuditLogService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class AuditLogController {

	@Autowired
	protected AuditLogService auditLogService;

	public AuditLogController() {
		super();
	}

	@GetMapping(name = "audit-log.index", path = "/audit-log/index")
	public String index(final ModelMap model) {
		return "audit-log/index";
	}

	@PostMapping(name = "audit-log.query", path = "/audit-log/query", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DataTableResponse query(final AuditLogTableRequest dataTableRequest) {
		try {
			Tuple2<Long, List<AuditLog>> queryTuple = auditLogService.query(dataTableRequest);
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
