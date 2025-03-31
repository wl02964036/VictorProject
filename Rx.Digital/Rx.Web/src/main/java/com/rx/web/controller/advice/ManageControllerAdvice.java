package com.rx.web.controller.advice;

import java.beans.PropertyEditorSupport;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.jooq.lambda.tuple.Tuple3;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.unbescape.html.HtmlEscape;
import org.unbescape.html.HtmlEscapeLevel;
import org.unbescape.html.HtmlEscapeType;

import com.rx.web.security.SystemUserAuthenticationToken;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ManageControllerAdvice {

	@Autowired
	protected List<Tuple3<String, String, String>> menuPathTuples;

	public ManageControllerAdvice() {
		super();
	}

	@InitBinder
	public void initBinder(WebDataBinder binder) {
		// String 類別轉換，將所有傳遞進來的String進行HTML編碼，防止XSS攻擊
		binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
			@Override
			public void setAsText(String text) {
				setValue(text == null ? null
						: HtmlEscape.escapeHtml(text.trim(), HtmlEscapeType.HTML5_NAMED_REFERENCES_DEFAULT_TO_DECIMAL,
								HtmlEscapeLevel.LEVEL_1_ONLY_MARKUP_SIGNIFICANT));
			}

			@Override
			public String getAsText() {
				Object value = getValue();
				return value != null ? value.toString() : "";
			}
		});
	}

	/**
	 * 接到 controller 的 request，先塞入共用的 model
	 *
	 * @param request             http servlet request
	 * @param authenticationToken 認證資訊
	 * @param model               要傳給 view 的 model
	 */
	@ModelAttribute
	public void handleRequest(final HttpServletRequest request, final SystemUserAuthenticationToken authenticationToken,
			final ModelMap model) {

		// requestURI = contextPath + servletPath + pathInfo
		String servletPath = request.getServletPath();
		log.info("servletPath is :'{}'", servletPath);

		if (authenticationToken != null && authenticationToken.isAuthenticated()) {
			model.addAttribute("loginUser", authenticationToken.getUser());
			model.addAttribute("sidebarMenus", authenticationToken.getSidebarMenus());

			Optional<Tuple3<String, String, String>> targetOpt = menuPathTuples.stream().filter(tuple3 -> {
				AntPathMatcher pathMatcher = new AntPathMatcher();
				return pathMatcher.match(tuple3.v2, servletPath);
			}).findAny();

			if (targetOpt.isPresent()) {
				model.addAttribute("currentPathTuple", targetOpt.get());
			}

		}

	}

}
