package com.rx.web.controller;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
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
import com.rx.core.bean.RoleUser;
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.dao.RoleUserDao;
import com.rx.core.dao.SystemUserDao;
import com.rx.core.util.DateUtil;
import com.rx.web.Exception.ElementNotFoundException;
import com.rx.web.modal.PasswordModel;
import com.rx.web.modal.UnitModel;
import com.rx.web.modal.UserModel;
import com.rx.web.security.SpecialGrantedAuthority;
import com.rx.web.service.AuditLogService;
import com.rx.web.service.SystemOrganizationService;
import com.rx.web.service.SystemRoleService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class OrganizationController {

	@Autowired
	protected SystemOrganizationService systemOrganizationService;

	@Autowired
	protected SystemRoleService systemRoleService;

	@Autowired
	protected AuditLogService auditLogService;

	@Autowired
	protected SystemUserDao systemUserDao;

	@Autowired
	protected RoleUserDao roleUserDao;

	public OrganizationController() {
		super();
	}

	@GetMapping(name = "organization.index", path = "/organization/index")
	public String index(final ModelMap model) {
		return "organization/index";
	}

	@GetMapping(name = "organization.newUnit", path = "/organization/newUnit")
	public String newUnit(@RequestParam(name = "node") String parentNode, final ModelMap model) {
		Long countOfChildren = systemOrganizationService.countUnitChildrenByParent(parentNode);

		UnitModel unitBind = new UnitModel();
		unitBind.setParentCode(parentNode);
		unitBind.setWeight(countOfChildren);

		model.addAttribute("unitBind", unitBind);
		return "organization/newUnit";
	}

	/* 檢查輸入的角色代號是否重複，ajax 回傳純字串，empty 表示無此角色代號，hasOne 表示有 */
	@GetMapping(name = "organization.unitCodeRepeat", path = "/organization/unitCodeRepeat", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String unitCodeRepeat(@RequestParam(name = "code") final String code) {
		Long amount = systemOrganizationService.countUnitByCode(code);
		if (amount > 0) {
			return "hasOne";
		} else {
			return "empty";
		}
	}

	@PostMapping(name = "organization.createUnit", path = "/organization/createUnit")
	public String createUnit(final HttpServletRequest request, final UnitModel unitBind, final ModelMap model,
			final RedirectAttributes redirectAttributes) {

		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
			systemOrganizationService.createUnit(unitBind, loginUser);

			auditLogService.create(request, model, AuditLog.TYPE_CREATE, unitBind.getCode().toUpperCase(),
					unitBind.getDisplayName(), "");

			redirectAttributes.addFlashAttribute("message", "儲存成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "儲存失敗");
		}

		return "redirect:/organization/editUnit?node=" + unitBind.getParentCode();
	}

	@GetMapping(name = "organization.editUnit", path = "/organization/editUnit")
	public String editUnit(@RequestParam(name = "node") String node, final ModelMap model,
			final RedirectAttributes redirectAttributes) {

		Optional<SystemUnit> unitOpt = systemOrganizationService.findUnitByCode(node);
		if (unitOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}

		SystemUnit unit = unitOpt.get();

		UnitModel unitBind = new UnitModel();
		unitBind.setCode(unit.getCode());
		unitBind.setDisplayName(unit.getDisplayName());
		unitBind.setFax(unit.getFax());
		unitBind.setEmail(unit.getEmail());
		unitBind.setTel(unit.getTel());
		unitBind.setWeight(unit.getWeight());
		unitBind.setParentCode(unit.getParent());

		model.addAttribute("unitBind", unitBind);

		return "organization/editUnit";
	}

	@PostMapping(name = "organization.updateUnit", path = "/organization/updateUnit")
	public String updateUnit(final HttpServletRequest request, final UnitModel unitBind, final ModelMap model,
			final RedirectAttributes redirectAttributes) {

		Optional<SystemUnit> unitOpt = systemOrganizationService.findUnitByCode(unitBind.getCode());
		if (unitOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "此單位不存在");
			return "redirect:/organization/index";
		}

		SystemUnit unit = unitOpt.get();

		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
			systemOrganizationService.updateUnit(unit, unitBind, loginUser);

			auditLogService.create(request, model, AuditLog.TYPE_UPDATE, unit.getCode(), unit.getDisplayName(), "");

			redirectAttributes.addFlashAttribute("message", "儲存成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "儲存失敗");
		}

		return "redirect:/organization/editUnit?node=" + unitBind.getCode();
	}

	@PostMapping(name = "organization.destroyUnit", path = "/organization/destroyUnit")
	public String destroyUnit(final @RequestParam(name = "code") String code, final HttpServletRequest request,
			final ModelMap model, final RedirectAttributes redirectAttributes) {

		Optional<SystemUnit> unitOpt = systemOrganizationService.findUnitByCode(code);
		if (unitOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "此單位不存在");
			return "redirect:/organization/index";
		}

		if (systemOrganizationService.countUnitChildrenByParent(code) > 0) {
			redirectAttributes.addFlashAttribute("message", "此單位尚有子單位，無法刪除");
			return "redirect:/organization/editUnit?node=" + code;
		}

		if (systemOrganizationService.countUsersByUnitCode(code) > 0) {
			redirectAttributes.addFlashAttribute("message", "此單位尚有人員，無法刪除");
			return "redirect:/organization/editUnit?node=" + code;
		}

		SystemUnit unit = unitOpt.get();

		try {
			systemOrganizationService.deleteUnit(unit.getCode());

			auditLogService.create(request, model, AuditLog.TYPE_DELETE, unit.getCode(), unit.getDisplayName(), "");

			redirectAttributes.addFlashAttribute("message", "刪除成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "刪除失敗");
		}

		return "redirect:/organization/editUnit?node=" + unit.getParent();
	}

	@GetMapping(name = "organization.newUser", path = "/organization/newUser")
	public String newUser(@RequestParam(name = "node") String parentNode, final ModelMap model) {
		List<Tuple2<String, String>> roleTuples;

		SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
		assert loginUser != null;
		if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.MANAGER)) {
			if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.ACTIVITYMANAGER)) {
				roleTuples = systemRoleService.findAllAssignableRoleTuples();
			} else {
				roleTuples = systemRoleService.findAllAssignableRoleTuplesExcludeAct();
			}
		} else {
			roleTuples = systemRoleService.findAllRoleTuples();
		}

		UserModel userBind = new UserModel();
		userBind.setUnitCode(parentNode);

		model.addAttribute("userBind", userBind);
		model.addAttribute("roleTuples", roleTuples);

		return "organization/newUser";
	}

	/* 檢查輸入的角色代號是否重複，ajax 回傳純字串，empty 表示無此角色代號，hasOne 表示有 */
	@GetMapping(name = "organization.usernameRepeat", path = "/organization/usernameRepeat", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String usernameRepeat(@RequestParam(name = "username") final String username) {
		Long amount = systemOrganizationService.countUserByUsername(username);
		if (amount > 0) {
			return "hasOne";
		} else {
			return "empty";
		}
	}

	@PostMapping(name = "organization.createUser", path = "/organization/createUser")
	public String createUser(final HttpServletRequest request, final UserModel userBind, final ModelMap model,
			final RedirectAttributes redirectAttributes) {
		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
			systemOrganizationService.createUser(userBind, loginUser);

			auditLogService.create(request, model, AuditLog.TYPE_CREATE, userBind.getUsername(),
					userBind.getDisplayName(), "");

			redirectAttributes.addFlashAttribute("message", "儲存成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "儲存失敗");
		}

		return "redirect:/organization/editUnit?node=" + userBind.getUnitCode();
	}

	@GetMapping(name = "organization.editUser", path = "/organization/editUser")
	public String editUser(@RequestParam(name = "node") String node, final ModelMap model) {
		Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(node);
		if (userOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}

		List<Tuple2<String, String>> roleTuples;

		SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
		assert loginUser != null;
		if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.MANAGER)) {
			if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.ACTIVITYMANAGER)) {
				roleTuples = systemRoleService.findAllAssignableRoleTuples();
			} else {
				roleTuples = systemRoleService.findAllAssignableRoleTuplesExcludeAct();
			}
		} else {
			roleTuples = systemRoleService.findAllRoleTuples();
		}

		SystemUser user = userOpt.get();
		String roles = systemOrganizationService.getRolesByUsername(node);

		UserModel userBind = new UserModel();
		userBind.setUsername(user.getUsername());
		userBind.setCode("");
		userBind.setConfirmCode("");
		userBind.setDisplayName(user.getDisplayName());
		userBind.setSex(user.getSex());
		userBind.setEmail(user.getEmail());
		userBind.setTel(user.getTel());
		userBind.setEnabled(user.isEnabled());
		userBind.setExpired(user.isExpired());
		userBind.setLocked(user.isLocked());
		userBind.setUnitCode(user.getUnitCode());
		userBind.setRoles(roles);

		model.addAttribute("userBind", userBind);
		model.addAttribute("roleTuples", roleTuples);

		return "organization/editUser";
	}

	@PostMapping(name = "organization.updateUser", path = "/organization/updateUser")
	public String updateUser(final HttpServletRequest request, final UserModel userBind, final ModelMap model,
			final RedirectAttributes redirectAttributes) {
		Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(userBind.getUsername());
		if (userOpt.isEmpty()) {
			redirectAttributes.addFlashAttribute("message", "此員工不存在");
			return "redirect:/organization/index";
		}

		SystemUser user = userOpt.get();

		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
			systemOrganizationService.updateUser(user, userBind, loginUser);

			auditLogService.create(request, model, AuditLog.TYPE_UPDATE, user.getUsername(), user.getDisplayName(), "");

			redirectAttributes.addFlashAttribute("message", "儲存成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "儲存失敗");
		}

		return "redirect:/organization/editUser?node=" + userBind.getUsername();
	}

	@PostMapping(name = "organization.destroyUser", path = "/organization/destroyUser")
	public String destroyUser(final @RequestParam(name = "username") String username,
			final @RequestParam(name = "unitCode") String unitCode, final HttpServletRequest request,
			final ModelMap model, final RedirectAttributes redirectAttributes) {

		Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(username);
		if (userOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}

		SystemUser user = userOpt.get();

		try {
			systemOrganizationService.deleteUser(user.getUsername());

			auditLogService.create(request, model, AuditLog.TYPE_DELETE, user.getUsername(), user.getDisplayName(), "");

			redirectAttributes.addFlashAttribute("message", "刪除成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "刪除失敗");
		}

		return "redirect:/organization/editUnit?node=" + unitCode;
	}

	@GetMapping(name = "organization.editPassword", path = "/organization/editPassword")
	public String editPassword(final @RequestParam(name = "username") String username, final ModelMap model) {
		Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(username);
		if (userOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}

		SystemUser user = userOpt.get();

		PasswordModel passwordBind = new PasswordModel();
		passwordBind.setUsername(user.getUsername());
		passwordBind.setDisplayName(user.getDisplayName());
		passwordBind.setCode("");
		passwordBind.setConfirmCode("");

		model.addAttribute("passwordBind", passwordBind);

		return "organization/editPassword";
	}

	@PostMapping(name = "organization.updatePassword", path = "/organization/updatePassword")
	public String updatePassword(final HttpServletRequest request, final PasswordModel passwordBind,
			final ModelMap model, final RedirectAttributes redirectAttributes) {
		Optional<SystemUser> userOpt = systemOrganizationService.findUserByUsername(passwordBind.getUsername());
		if (userOpt.isEmpty()) {
			throw new ElementNotFoundException();
		}

		SystemUser user = userOpt.get();

		try {
			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
			systemOrganizationService.updateUserPassword(user, passwordBind, loginUser);

			auditLogService.create(request, model, AuditLog.TYPE_UPDATE, user.getUsername(), user.getDisplayName(),
					"修改密碼");

			redirectAttributes.addFlashAttribute("message", "密碼修改成功");
		} catch (Exception e) {
			log.error("", e);
			redirectAttributes.addFlashAttribute("message", "密碼修改失敗");
		}

		return "redirect:/organization/editUser?node=" + passwordBind.getUsername();
	}

	/* 解除密碼錯誤鎖定，ajax 回傳純字串，success 表示成功 */
	@PostMapping(name = "organization.unlock", path = "/organization/unlock", produces = MediaType.TEXT_PLAIN_VALUE)
	@ResponseBody
	public String unlock(@RequestParam(name = "username") final String username) {
		try {
			boolean result = systemOrganizationService.resetLoginErrorsAndLockedAtByUsername(username);
			if (result) {
				return "success";
			} else {
				return "failure";
			}
		} catch (Exception e) {
			log.error("", e);
			return "failure";
		}
	}

	@PostMapping(name = "organization.report", path = "/organization/report")
	public void report(final HttpServletResponse response, final ModelMap model) {

		// 如果登入者是機關管理者，以此人的單位為 root 單位
		SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");

		if (loginUser != null) {
			if (loginUser.getAuthorities().contains(SpecialGrantedAuthority.ADMIN)) {
			} else {
				return;
			}
		}

		List<SystemUser> systemUsers = systemUserDao.findAll();

		HSSFWorkbook workbook = new HSSFWorkbook();
		HSSFSheet sheet = workbook.createSheet("管理端使用者列表");

		HSSFFont headFont = workbook.createFont();
		headFont.setBold(true);
		headFont.setFontHeightInPoints((short) 14);

		HSSFCellStyle centerStyle = workbook.createCellStyle();
		centerStyle.setAlignment(HorizontalAlignment.CENTER);
		centerStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		centerStyle.setFont(headFont);

		HSSFFont itemFont = workbook.createFont();
		itemFont.setBold(true);
		itemFont.setFontHeightInPoints((short) 10);

		HSSFCellStyle itemStyle = workbook.createCellStyle();
		itemStyle.setFont(itemFont);

		sheet.setColumnWidth(0, 24 * 256);
		sheet.setColumnWidth(1, 24 * 256);
		sheet.setColumnWidth(2, 24 * 256);
		sheet.setColumnWidth(3, 24 * 256);
		sheet.setColumnWidth(4, 24 * 256);
		sheet.setColumnWidth(5, 24 * 256);
		sheet.setColumnWidth(6, 24 * 256);
		sheet.setColumnWidth(7, 24 * 256);
		sheet.setColumnWidth(8, 24 * 256);

		HSSFCell cell;
		HSSFRow row = sheet.createRow(0);
		cell = row.createCell(0);
		cell.setCellStyle(centerStyle);
		cell.setCellValue("管理端使用者列表");
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 5));

		row = sheet.createRow(1);
		row.createCell(0).setCellValue("帳號");
		row.createCell(1).setCellValue("權限");
		row.createCell(2).setCellValue("建立日期");
		row.createCell(3).setCellValue("姓名");
		row.createCell(4).setCellValue("是否啟用");

		int rowNumber = 2;
		int indexNumber = 1;
		for (SystemUser systemUser : systemUsers) {
			row = sheet.createRow(rowNumber);

			List<RoleUser> roleUsers = roleUserDao.findByUserName(systemUser.getUsername());
			String role = "";

			for (RoleUser roleUser : roleUsers) {
				role = role + roleUser.getCode() + ", ";
			}
			role = role.substring(0, role.length() - 2);

			row.createCell(0).setCellValue(systemUser.getUsername());
			row.createCell(1).setCellValue(role);
			row.createCell(2).setCellValue(DateUtil.getDate(systemUser.getCreatedAt()));
			row.createCell(3).setCellValue(systemUser.getDisplayName());
			row.createCell(4).setCellValue(systemUser.isEnabled());

			indexNumber = indexNumber + 1;
			rowNumber = rowNumber + 1;
		}

		rowNumber += 2;
		row = sheet.createRow(rowNumber);
		row.createCell(0).setCellValue("代號");
		row.createCell(1).setCellValue("名稱");
		rowNumber += 1;

		List<Tuple2<String, String>> roleTuples = systemRoleService.findAllRoleTuples();
		for (Tuple2<String, String> tuple : roleTuples) {
			row = sheet.createRow(rowNumber);

			row.createCell(0).setCellValue(tuple.v1);
			row.createCell(1).setCellValue(tuple.v2);

			rowNumber = rowNumber + 1;
		}

		try {
			String filename = "report.xls";
			response.setContentType("application/vnd.ms-excel");
			response.setHeader("Content-Disposition",
					"attachment;filename=" + URLEncoder.encode(filename, StandardCharsets.UTF_8));
			try (OutputStream outputStream = response.getOutputStream()) {
				workbook.write(outputStream);
				outputStream.flush();
			}
		} catch (Exception e) {
			log.error("", e);
			return;
		}
	}

}