package com.rx.webapi.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.support.collect.Maps;
import com.rx.core.support.datatable.DataTableResponse;
import com.rx.core.table.request.PermissionTableRequest;
import com.rx.webapi.model.PermissionQueryModel;
import com.rx.webapi.model.UserModel;
import com.rx.webapi.security.SpecialGrantedAuthority;
import com.rx.webapi.service.AuditLogService;
import com.rx.webapi.service.SystemOrganizationService;
import com.rx.webapi.service.SystemRoleService;
import com.rx.webapi.service.SystemUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@RequestMapping("/angular")
public class PermissionApiController {

	@Autowired
	protected SystemUserService systemUserService;

	@Autowired
	protected SystemOrganizationService systemOrganizationService;

	@Autowired
	protected SystemRoleService systemRoleService;

	@Autowired
	protected AuditLogService auditLogService;

	@PostMapping(name = "permission.query", path = "/permission/query", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DataTableResponse query(final PermissionTableRequest dataTableRequest,
			@AuthenticationPrincipal final SystemUser loginUser) {
		try {
			Tuple2<Long, List<PermissionQueryModel>> queryTuple;

			if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.MANAGER)) {
				List<String> unitCodes = systemOrganizationService
						.findChildrenAndIncludeUnitCode(loginUser.getUnitCode());
				queryTuple = systemUserService.queryIncludeUnitsOnly(dataTableRequest, unitCodes);
			} else {
				queryTuple = systemUserService.query(dataTableRequest);
			}

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

	@GetMapping(name = "permission.edit", path = "/permission/edit")
	public void edit(@RequestParam(name = "username") String username, final HttpServletRequest request,
			final HttpServletResponse response, @AuthenticationPrincipal final SystemUser loginUser) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {

			Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(username);
			if (userOpt.isEmpty()) {
				map.put("status", "error");
				map.put("message", "此員工不存在");
				responseOutput(response, map);
				return;
			}

			SystemUser user = userOpt.get();

			List<Tuple2<String, String>> roleTuples;

			if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.MANAGER)) {
				roleTuples = systemRoleService.findAllAssignableRoleTuples();
			} else {
				roleTuples = systemRoleService.findAllRoleTuples();
			}

			String roles = systemOrganizationService.getRolesByUsername(username);

			UserModel userBind = new UserModel();
			userBind.setUsername(user.getUsername());
			userBind.setDisplayName(user.getDisplayName());
			userBind.setUnitCode(user.getUnitCode());
			userBind.setRoles(roles);

			String unitName = "";
			Optional<SystemUnit> unitOpt = systemOrganizationService.findUnitByCode(user.getUnitCode());
			if (unitOpt.isPresent()) {
				unitName = unitOpt.get().getDisplayName();
			}

			map.put("status", "success");
			map.put("message", "查詢成功");
			map.put("user", userBind);
			map.put("roleTuples", roleTuples);
			map.put("unitName", unitName);
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

	@PostMapping(name = "permission.update", path = "/permission/update")
	public void update(@RequestBody final UserModel userBind, final HttpServletRequest request, final HttpServletResponse response,
			@AuthenticationPrincipal final SystemUser loginUser) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {

			Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(userBind.getUsername());
			if (userOpt.isEmpty()) {
				map.put("status", "error");
				map.put("message", "此角色不存在");
				responseOutput(response, map);
				return;
			}

			SystemUser user = userOpt.get();

			systemUserService.updateRoles(user, userBind, loginUser);

			auditLogService.create(request, loginUser, AuditLog.TYPE_UPDATE, user.getUsername(), user.getDisplayName(),
					"修改角色");

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
