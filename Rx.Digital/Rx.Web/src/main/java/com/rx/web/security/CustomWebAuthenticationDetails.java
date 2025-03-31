package com.rx.web.security;

import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;
	
    public static final String CaptchaAnswerAttrName = "@_CaptchaAnswer_@";

    private final String captcha;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        captcha = Objects.toString(request.getParameter("captcha"), "");
    }

    public String getCaptcha() {
        return captcha;
    }

}
