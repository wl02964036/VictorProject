package com.rx.webapi.controller;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.rx.core.bean.LoginLog;
import com.rx.core.bean.RoleMenu;
import com.rx.core.bean.RoleUser;
import com.rx.core.bean.SystemMenu;
import com.rx.core.bean.SystemUnit;
import com.rx.core.bean.SystemUser;
import com.rx.core.dao.RoleMenuDao;
import com.rx.core.dao.RoleUserDao;
import com.rx.core.dao.SystemMenuDao;
import com.rx.core.dao.SystemUnitDao;
import com.rx.core.dao.SystemUserDao;
import com.rx.core.support.AuthoritySupport;
import com.rx.core.support.SidebarMenu;
import com.rx.core.support.collect.Lists;
import com.rx.core.support.collect.Maps;
import com.rx.core.util.JwtUtil;
import com.rx.webapi.exception.BadCaptchaException;
import com.rx.webapi.request.LoginRequest;
import com.rx.webapi.security.CustomWebAuthenticationDetails;
import com.rx.webapi.service.LoginLogService;
import com.rx.webapi.service.SystemRoleService;
import com.rx.webapi.service.SystemUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginAPIController {

	@Autowired
	protected SystemUserDao systemUserDao;

	@Autowired
	protected SystemUnitDao systemUnitDao;

	@Autowired
	protected RoleUserDao roleUserDao;

	@Autowired
	protected SystemMenuDao systemMenuDao;

	@Autowired
	protected RoleMenuDao roleMenuDao;

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected JwtUtil jwtUtil;

	@Autowired
	protected SystemUserService systemUserService;

	@Autowired
	protected LoginLogService loginLogService;

	@Autowired
	protected SystemRoleService systemRoleService;

	@PostMapping(name = "angular.login", path = "/angular/login")
	public void login(@RequestBody final LoginRequest request, final HttpServletRequest httpRequest,
			final HttpServletResponse response) {

		Map<String, Object> map = Maps.newLinkedHashMap();

		try {
			// 接收參數
			String username = request.getUsername();
			String password = request.getPassword();
			String captcha = request.getCaptcha();

			if (captcha == null || !validateCaptcha(captcha)) {
				throw new BadCaptchaException("驗證碼不正確");
			}

			Optional<SystemUser> userOpt = systemUserDao.findByUserName(username);
			if (userOpt.isEmpty()) {
				throw new UsernameNotFoundException(username + " 帳號不存在");
			}

			SystemUser userDetails = userOpt.get();

			if (userDetails.getLockedAt() != null) {
				LocalDateTime lockedAt = userDetails.getLockedAt();
				LocalDateTime unLockAt = lockedAt.plusMinutes(10);
				if (LocalDateTime.now().isBefore(unLockAt)) {
					throw new LockedException("帳號鎖定中");
				}
			}

			// 載入 User 的單位
			Optional<SystemUnit> unitOpt = systemUnitDao.findByCode(userDetails.getUnitCode());
			if (unitOpt.isPresent()) {
				userDetails.setUnit(unitOpt.get());
			} else {
				log.error("{}", "無法找到" + username + "的部門");
				userDetails.setUnit(null);
			}

			int loginErrors = 0;
			boolean passed = passwordEncoder.matches(password, userDetails.getPassword());
			if (!passed) {

				// 加上密碼登入錯誤次數
				Optional<Integer> loginErrorsOpt = systemUserDao.findLoginErrorsByUsername(userDetails.getUsername());

				if (loginErrorsOpt.isPresent()) {
					loginErrors = loginErrorsOpt.get();
				}

				loginErrors = loginErrors + 1;
				try {
					systemUserService.setLoginErrorsByUsername(loginErrors, userDetails.getUsername());
				} catch (Exception e) {
					log.error("", e);
				}

				// 錯誤3次，鎖定10分鐘
				if (loginErrors > 2) {
					// 鎖定10分鐘
					try {
						systemUserService.setLockedAtByUsername(userDetails.getUsername());
					} catch (Exception e) {
						log.error("", e);
					}
					throw new BadCredentialsException("密碼錯誤太多次，請過10分鐘後再登入");
				}

				throw new BadCredentialsException("密碼不正確，錯誤" + loginErrors + "次");
			}

			if (!userDetails.isEnabled()) {
				throw new DisabledException("帳號尚未啟用，或是停用中");
			}

			if (!userDetails.isAccountNonLocked()) {
				throw new LockedException("帳號鎖定中");
			}

			if (!userDetails.isAccountNonExpired()) {
				throw new AccountExpiredException("密碼已過期");
			}

			// 故意清除密碼欄位，不要存在 memory 中
			userDetails.eraseCredentials();

			List<RoleUser> rolesByUser = roleUserDao.findByUserName(username);
			userDetails.setAuthorities(AuthoritySupport.getAuthorities(rolesByUser));

			// 找出此User允許存取的 所有 menu，利用 Set 濾掉重複的 path
			Set<SystemMenu> authorityMenus = new LinkedHashSet<>();
			for (GrantedAuthority authority : userDetails.getAuthorities()) {
				String roleCode = authority.getAuthority();
				List<RoleMenu> menus = roleMenuDao.findByCode(roleCode);
				for (RoleMenu rm : menus) {
					Optional<SystemMenu> systemMenuOpt = systemMenuDao.findByUUID(rm.getUuid());
					if (systemMenuOpt.isPresent()) {
						authorityMenus.add(systemMenuOpt.get());
					}
				}
			}
			
			boolean isAngular = true;
			List<SidebarMenu> sidebarMenus = AuthoritySupport.buildSidebarMenus(systemMenuDao, authorityMenus, isAngular);

			try {
				systemUserService.resetLoginErrorsAndLockedAtByUsername(userDetails.getUsername());

				loginLogService.create(httpRequest, username, LoginLog.STATUS_SUCCEED, "");

			} catch (Exception e) {
				// ignore
				log.error("loginLog:", e);
			}

			// 可以驗證帳密
			Map<String, Object> claims = new HashMap<>();
			claims.put("username", username);

			String token = jwtUtil.generateToken(username, claims);

			map.put("status", "success");
			map.put("message", "儲存成功");
			map.put("token", token);
			map.put("systemUser", userDetails);
			map.put("sidebarMenus", sidebarMenus);
			responseOutput(response, map);
			return;
		} catch (Exception e) {
			log.error("", e);

			map.put("status", "error");
			map.put("message", e.getMessage());
			responseOutput(response, map);
			return;
		}
	}

	private boolean validateCaptcha(String code) throws Exception {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		HttpSession session = request.getSession(false);
		if (session != null) {
			String answer = (String) session.getAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName);
			if (answer == null) {
				return false;
			}
			return answer.equals(code);
		} else {
			throw new Exception("驗證碼未正確顯示，請刷新畫面");
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
