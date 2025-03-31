package com.rx.web.controller;

import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.tuple.Tuple2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.rx.core.bean.AuditLog;
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.support.datatable.DataTableResponse;
import com.rx.core.table.request.PermissionTableRequest;
import com.rx.web.Exception.ElementNotFoundException;
import com.rx.web.modal.PermissionQueryModel;
import com.rx.web.modal.UserModel;
import com.rx.web.security.SpecialGrantedAuthority;
import com.rx.web.service.AuditLogService;
import com.rx.web.service.SystemOrganizationService;
import com.rx.web.service.SystemRoleService;
import com.rx.web.service.SystemUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class PermissionController {

	@Autowired
	protected SystemUserService systemUserService;

	@Autowired
	protected SystemOrganizationService systemOrganizationService;

	@Autowired
	protected SystemRoleService systemRoleService;

	@Autowired
	protected AuditLogService auditLogService;

	public PermissionController() {
		super();
	}

	@GetMapping(name = "permission.index", path = "/permission/index")
	public String index(final ModelMap model) {
		return "permission/index";
	}

	@PostMapping(name = "permission.query", path = "/permission/query", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public DataTableResponse query(final PermissionTableRequest dataTableRequest, final ModelMap model) {
		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");

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
	public String edit(@RequestParam(name = "username") String username, final ModelMap model) {

		Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(username);
		if (userOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}
		SystemUser user = userOpt.get();

		List<Tuple2<String, String>> roleTuples;

		SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
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

		model.addAttribute("userBind", userBind);
		model.addAttribute("roleTuples", roleTuples);

		Optional<SystemUnit> unitOpt = systemOrganizationService.findUnitByCode(user.getUnitCode());
		if (unitOpt.isPresent()) {
			model.addAttribute("unitName", unitOpt.get().getDisplayName());
		} else {
			model.addAttribute("unitName", "");
		}
		return "permission/edit";
	}

	@PostMapping(name = "permission.update", path = "/permission/update")
	public String update(final HttpServletRequest request, final UserModel userBind, final ModelMap model,
			final RedirectAttributes redirectAttributes) {

		Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(userBind.getUsername());
		if (userOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "此員工不存在");
			return "redirect:/permission/index";
		}

		SystemUser user = userOpt.get();

		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
			systemUserService.updateRoles(user, userBind, loginUser);

			auditLogService.create(request, model, AuditLog.TYPE_UPDATE, user.getUsername(), user.getDisplayName(),
					"修改角色");

			redirectAttributes.addFlashAttribute("message", "儲存成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "儲存失敗");
		}

		return "redirect:/permission/index";
	}

}
