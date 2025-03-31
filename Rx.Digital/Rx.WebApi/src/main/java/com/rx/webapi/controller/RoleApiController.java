package com.rx.webapi.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.rx.core.bean.AuditLog;
import com.rx.core.bean.SystemRole;
import com.rx.core.bean.SystemUser;
import com.rx.core.support.collect.Maps;
import com.rx.core.support.datatable.DataTableRequest;
import com.rx.core.support.datatable.DataTableResponse;
import com.rx.core.table.request.RoleTableRequest;
import com.rx.webapi.model.RoleModel;
import com.rx.webapi.model.RoleQueryModel;
import com.rx.webapi.service.AuditLogService;
import com.rx.webapi.service.SystemRoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/angular")
public class RoleApiController {

	@Autowired
	protected SystemRoleService systemRoleService;

	@Autowired
	protected AuditLogService auditLogService;

	@PostMapping(name = "role.query", path = "/role/query", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DataTableResponse query(final RoleTableRequest dataTableRequest) {
		try {
			Tuple2<Long, List<RoleQueryModel>> queryTuple = systemRoleService.query(dataTableRequest);
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

	/* 檢查輸入的角色代號是否重複，ajax 回傳純字串，empty 表示無此角色代號，hasOne 表示有 */
	@GetMapping(name = "role.codeRepeat", path = "/role/codeRepeat", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String codeRepeat(@RequestParam(name = "code") final String code) {
		Long amount = systemRoleService.countByCode(code);
		if (amount > 0) {
			return "hasOne";
		} else {
			return "empty";
		}
	}

	@GetMapping(name = "row.new", path = "/role/new")
	public void newRole(final HttpServletRequest httpRequest, final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			map.put("status", "success");
			map.put("message", "查詢成功");
			map.put("role", new RoleModel());
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@PostMapping(name = "role.create", path = "/role/create")
	public void create(@RequestBody final RoleModel roleBind, final HttpServletRequest request, final HttpServletResponse response,
			@AuthenticationPrincipal final SystemUser loginUser) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {

			// 目前這邊完全仰賴 parsley.js 做 client 端驗證，嚴謹的話，後端應該也要驗證
			log.info("{}", roleBind);

			// 將代碼大寫
			String code = roleBind.getCode().toUpperCase();
			roleBind.setCode(code);
			// spring security 預設需要用 "ROLE_" 做為group的開頭
			if (!roleBind.getCode().startsWith("ROLE_")) {
				roleBind.setCode("ROLE_" + roleBind.getCode());
			}

			systemRoleService.createRole(roleBind, loginUser);

			auditLogService.create(request, loginUser, AuditLog.TYPE_CREATE, roleBind.getCode(), roleBind.getTitle(),
					"");

			map.put("status", "success");
			map.put("message", "儲存成功");
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "儲存失敗");
			responseOutput(response, map);
			return;
		}
	}

	@GetMapping(name = "role.edit", path = "/role/edit")
	public void edit(@RequestParam(name = "code") final String code, final HttpServletRequest request,
			final HttpServletResponse response, @AuthenticationPrincipal final SystemUser loginUser) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {

			Optional<SystemRole> roleOpt = systemRoleService.findRoleByCode(code);
			if (roleOpt.isEmpty()) {
				map.put("status", "error");
				map.put("message", "此角色不存在");
				responseOutput(response, map);
				return;
			}

			SystemRole role = roleOpt.get();

			RoleModel roleBind = new RoleModel();
			roleBind.setCode(role.getCode());
			roleBind.setTitle(role.getTitle());
			roleBind.setDescription(role.getDescription());
			roleBind.setAssignable(role.getAssignable());
			roleBind.setItems(systemRoleService.findUUIDsByCode(role.getCode()));

			map.put("status", "success");
			map.put("message", "查詢成功");
			map.put("role", roleBind);
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "查詢失敗");
			responseOutput(response, map);
			return;
		}
	}

	@PostMapping(name = "role.update", path = "/role/update")
	public void update(@RequestBody final RoleModel roleBind, final HttpServletRequest request, final HttpServletResponse response,
			@AuthenticationPrincipal final SystemUser loginUser) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {

			// 目前這邊完全仰賴 parsley.js 做 client 端驗證，嚴謹的話，後端應該也要驗證
			log.info("{}", roleBind);

			Optional<SystemRole> roleOpt = systemRoleService.findRoleByCode(roleBind.getCode());
			if (roleOpt.isEmpty()) {
				map.put("status", "error");
				map.put("message", "此角色不存在");
				responseOutput(response, map);
				return;
			}

			try {
				systemRoleService.updateRole(roleOpt.get(), roleBind, loginUser);

				auditLogService.create(request, loginUser, AuditLog.TYPE_UPDATE, roleOpt.get().getCode(),
						roleOpt.get().getTitle(), "");

				map.put("status", "success");
				map.put("message", "儲存成功");
				responseOutput(response, map);
			} catch (Exception e) {
				log.error("", e);

				map.put("status", "error");
				map.put("message", "儲存失敗");
				responseOutput(response, map);
				return;
			}
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	@PostMapping(name = "role.destroy", path = "/role/destroy")
	public void destroy(final @RequestParam(name = "roles") String roles, final HttpServletRequest request,
			final HttpServletResponse response, @AuthenticationPrincipal final SystemUser loginUser) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {

			log.info("{}", roles);

			String[] roleItems = roles.split(",");

			try {
				systemRoleService.destroy(Arrays.asList(roleItems));

				for (String r : roleItems) {
					auditLogService.create(request, loginUser, AuditLog.TYPE_DELETE, r, r, "");
				}

				map.put("status", "success");
				map.put("message", "刪除成功");
				responseOutput(response, map);
				return;
			} catch (Exception e) {
				log.error("", e);

				map.put("status", "error");
				map.put("message", "刪除失敗");
				responseOutput(response, map);
				return;
			}
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", "Error : " + e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	public void responseOutput(final HttpServletResponse response, Map<String, Object> map) {
		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setContentType("application/json; charset=UTF-8");
		response.setStatus(HttpServletResponse.SC_OK);
		try {
			ServletOutputStream out = response.getOutputStream();
			out.write(Maps.toJson(map).getBytes(StandardCharsets.UTF_8));
			out.flush();
			out.close();
		} catch (IOException ex) {
			log.error("close output stream error");
		}
	}

}
