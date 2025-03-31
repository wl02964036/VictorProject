package com.rx.core.support;

import javax.servlet.http.HttpServletRequest;

public final class ClientIPSupport {

	public static String getIPFromXFHeader(final HttpServletRequest request) {
		String xfHeader = request.getHeader("X-Forwarded-For");
		if (xfHeader == null) {
			return request.getRemoteAddr();
		}
		return xfHeader.split(",")[0];
	}

	private ClientIPSupport() {
		super();
	}

}
