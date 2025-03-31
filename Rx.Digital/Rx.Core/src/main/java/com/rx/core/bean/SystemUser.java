package com.rx.core.bean;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class SystemUser extends Auditable implements Serializable, UserDetails, CredentialsContainer {

	private static final long serialVersionUID = 1L;

	private String username;
	private String password;
	private String displayName;
	private String sex;
	private String email;
	private String tel;
	private boolean enabled;
	private boolean expired;
	private boolean locked;
	private String unitCode;
	private LocalDateTime pwdUpdateAt;
	private int loginErrors;
	private LocalDateTime lockedAt;

	private Collection<? extends GrantedAuthority> authorities = new ArrayList<>();
	private SystemUnit unit;

	@Override
	public void eraseCredentials() {
		this.password = null;
	}

	@Override
	public boolean isAccountNonExpired() {
		return !expired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return !locked;
	}

	// 密碼是否過期
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

}
