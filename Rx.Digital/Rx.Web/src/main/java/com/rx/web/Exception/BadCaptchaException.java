package com.rx.web.Exception;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityCoreVersion;

public class BadCaptchaException extends AuthenticationException {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

	public BadCaptchaException(String msg, Throwable t) {
		super(msg, t);
	}

	public BadCaptchaException(String msg) {
		super(msg);
	}

}
