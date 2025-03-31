package com.rx.web.security;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import com.rx.web.Exception.BadCaptchaException;
import com.rx.web.service.LoginLogService;
import com.rx.web.service.SystemUserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomAuthenticationProvider implements AuthenticationProvider {

	@Autowired
	protected PasswordEncoder passwordEncoder;

	@Autowired
	protected SystemUserDao systemUserDao;

	@Autowired
	protected SystemUnitDao systemUnitDao;

	@Autowired
	protected SystemMenuDao systemMenuDao;

	@Autowired
	protected RoleUserDao roleUserDao;

	@Autowired
	protected RoleMenuDao roleMenuDao;

	@Autowired
	protected LoginLogService loginLogService;

	@Autowired
	protected SystemUserService systemUserService;

	public CustomAuthenticationProvider() {
		super();
	}

	private boolean validateCaptcha(String code) {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		String answer = (String) request.getSession(false)
				.getAttribute(CustomWebAuthenticationDetails.CaptchaAnswerAttrName);
		if (answer == null) {
			return false;
		}
		return answer.equals(code);
	}

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
				.getRequest();
		// log.info("{}", authentication.getClass());

		CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
		String captcha = details.getCaptcha();

		if (captcha == null || !validateCaptcha(captcha)) {
			throw new BadCaptchaException("驗證碼不正確");
		}

		String username = Objects.toString(authentication.getName(), "");
		String password = Objects.toString(authentication.getCredentials(), "");

		// log.info("{}", username);
		// log.info("{}", password);

		Optional<SystemUser> userOpt = systemUserDao.findByUserName(username);
		if (userOpt.isEmpty()) {
			throw new UsernameNotFoundException(username + " 帳號不存在");
		}

		SystemUser userDetails = userOpt.get();
		log.info("{}", userDetails);

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
			// log.info("{}", roleCode);
			List<RoleMenu> menus = roleMenuDao.findByCode(roleCode);
			for (RoleMenu rm : menus) {
				Optional<SystemMenu> systemMenuOpt = systemMenuDao.findByUUID(rm.getUuid());
				if (systemMenuOpt.isPresent()) {
					authorityMenus.add(systemMenuOpt.get());
				}
			}
		}

		
		boolean isAngular = false;
		List<SidebarMenu> sidebarMenus = AuthoritySupport.buildSidebarMenus(systemMenuDao, authorityMenus, isAngular);
		// log.info("{}", sidebarMenus);

		try {
			systemUserService.resetLoginErrorsAndLockedAtByUsername(userDetails.getUsername());

			loginLogService.create(request, Objects.toString(authentication.getName(), ""), LoginLog.STATUS_SUCCEED,
					"");

		} catch (Exception e) {
			// ignore
			log.error("loginLog:", e);
		}

		return new SystemUserAuthenticationToken(username, password, userDetails, sidebarMenus,
				userDetails.getAuthorities());
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
	}

}