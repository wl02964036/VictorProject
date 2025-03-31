package com.rx.web.controller;

import java.util.Arrays;
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
import com.rx.core.bean.SystemRole;
import com.rx.core.bean.SystemUser;
import com.rx.core.support.datatable.DataTableResponse;
import com.rx.core.table.request.RoleTableRequest;
import com.rx.web.Exception.ElementNotFoundException;
import com.rx.web.modal.RoleModel;
import com.rx.web.modal.RoleQueryModel;
import com.rx.web.service.AuditLogService;
import com.rx.web.service.SystemRoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class RoleController {

	@Autowired
	protected SystemRoleService systemRoleService;

	@Autowired
	protected AuditLogService auditLogService;

	public RoleController() {
		super();
	}

	@GetMapping(name = "role.index", path = "/role/index")
	public String index(final ModelMap model) {
		log.info("{}", model);
		return "role/index";
	}

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
	public String newRole(final ModelMap model) {
		log.info("{}", model);
		model.addAttribute("roleBind", new RoleModel());
		return "role/new";
	}

	@PostMapping(name = "role.create", path = "/role/create")
	public String create(final HttpServletRequest request, final RoleModel roleBind, final ModelMap model,
			final RedirectAttributes redirectAttributes) {

		// 目前這邊完全仰賴 parsley.js 做 client 端驗證，嚴謹的話，後端應該也要驗證
		log.info("{}", roleBind);

		// 將代碼大寫
		String code = roleBind.getCode().toUpperCase();
		roleBind.setCode(code);
		// spring security 預設需要用 "ROLE_" 做為group的開頭
		if (!roleBind.getCode().startsWith("ROLE_")) {
			roleBind.setCode("ROLE_" + roleBind.getCode());
		}

		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
			systemRoleService.createRole(roleBind, loginUser);

			auditLogService.create(request, model, AuditLog.TYPE_CREATE, roleBind.getCode(), roleBind.getTitle(), "");

			redirectAttributes.addFlashAttribute("message", "儲存成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "儲存失敗");
		}

		return "redirect:/role/index";
	}

	@GetMapping(name = "role.edit", path = "/role/edit")
	public String edit(@RequestParam(name = "code") final String code, final ModelMap model) {

		Optional<SystemRole> roleOpt = systemRoleService.findRoleByCode(code);
		if (roleOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}

		SystemRole role = roleOpt.get();

		RoleModel roleBind = new RoleModel();
		roleBind.setCode(role.getCode());
		roleBind.setTitle(role.getTitle());
		roleBind.setDescription(role.getDescription());
		roleBind.setAssignable(role.getAssignable());
		roleBind.setItems(systemRoleService.findUUIDsByCode(role.getCode()));

		model.addAttribute("roleBind", roleBind);
		return "role/edit";
	}

	@PostMapping(name = "role.update", path = "/role/update")
	public String update(final HttpServletRequest request, final RoleModel roleBind, final ModelMap model,
			final RedirectAttributes redirectAttributes) {

		// 目前這邊完全仰賴 parsley.js 做 client 端驗證，嚴謹的話，後端應該也要驗證
		log.info("{}", roleBind);

		Optional<SystemRole> roleOpt = systemRoleService.findRoleByCode(roleBind.getCode());
		if (roleOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "此角色不存在");
			return "redirect:/role/index";
		}

		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
			systemRoleService.updateRole(roleOpt.get(), roleBind, loginUser);

			auditLogService.create(request, model, AuditLog.TYPE_UPDATE, roleOpt.get().getCode(),
					roleOpt.get().getTitle(), "");

			redirectAttributes.addFlashAttribute("message", "儲存成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "儲存失敗");
		}
		return "redirect:/role/index";
	}

	@PostMapping(name = "role.destroy", path = "/role/destroy")
	public String destroy(final @RequestParam(name = "roles") String roles, final HttpServletRequest request,
			final ModelMap model, final RedirectAttributes redirectAttributes) {

		log.info("{}", roles);

		String[] roleItems = roles.split(",");

		try {
			systemRoleService.destroy(Arrays.asList(roleItems));

			for (String r : roleItems) {
				auditLogService.create(request, model, AuditLog.TYPE_DELETE, r, r, "");
			}

			redirectAttributes.addFlashAttribute("message", "刪除成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "刪除失敗");
		}

		return "redirect:/role/index";
	}

}
