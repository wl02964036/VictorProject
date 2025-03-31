package com.rx.web.security;

import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;

import com.rx.core.bean.SystemUser;
import com.rx.core.support.SidebarMenu;

public class SystemUserAuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	private final SystemUser user;
	private final List<SidebarMenu> sidebarMenus;

	public SystemUserAuthenticationToken(Object principal, Object credentials, SystemUser user,
			List<SidebarMenu> menus) {
		super(principal, credentials);
		this.user = user;
		this.sidebarMenus = menus;
	}

	public SystemUserAuthenticationToken(Object principal, Object credentials, SystemUser user, List<SidebarMenu> menus,
			Collection<? extends GrantedAuthority> authorities) {
		super(principal, credentials, authorities);
		this.user = user;
		this.sidebarMenus = menus;
	}

	public SystemUser getUser() {
		return user;
	}

	public List<SidebarMenu> getSidebarMenus() {
		return sidebarMenus;
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", SystemUserAuthenticationToken.class.getSimpleName() + "[", "]")
				.add("super='" + super.toString() + "'").add("user=" + user).toString();
	}

}
