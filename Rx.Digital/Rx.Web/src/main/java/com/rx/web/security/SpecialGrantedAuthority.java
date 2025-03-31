package com.rx.web.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

public final class SpecialGrantedAuthority {

	public static final SimpleGrantedAuthority ADMIN = new SimpleGrantedAuthority("ROLE_ADMIN");

	public static final SimpleGrantedAuthority MANAGER = new SimpleGrantedAuthority("ROLE_MANAGER");

	public static final SimpleGrantedAuthority ACTIVITYMANAGER = new SimpleGrantedAuthority("ROLE_ACTIVITYMANAGER");
}