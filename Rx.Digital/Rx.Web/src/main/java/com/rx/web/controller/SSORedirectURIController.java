//package com.rx.web.controller;
//
//import java.util.List;
//import java.util.Optional;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.jooq.lambda.tuple.Tuple2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.MediaType;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.ModelMap;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.ResponseBody;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
//
//import com.rx.core.bean.AuditLog;
//import com.rx.core.bean.SystemUser;
//import com.rx.core.support.datatable.DataTableRequest;
//import com.rx.core.support.datatable.DataTableResponse;
//import com.rx.web.Exception.ElementNotFoundException;
//import com.rx.web.service.AuditLogService;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Controller
//public class SSORedirectURIController {
//
//	@Autowired
//	protected APIClientServiceImp apiClientService;
//
//	@Autowired
//	protected AuditLogService auditLogService;
//
//	public SSORedirectURIController() {
//		super();
//	}
//
//	@GetMapping(name = "sso-uri.index", path = "/sso-uri/index")
//	public String index(final ModelMap model) {
//		return "sso-uri/index";
//	}
//
//	@PostMapping(name = "sso-uri.query", path = "/sso-uri/query", produces = MediaType.APPLICATION_JSON_VALUE)
//	@ResponseBody
//	public DataTableResponse query(final DataTableRequest dataTableRequest) {
//		try {
//			Tuple2<Long, List<APIClientQueryViewModel>> queryTuple = apiClientService.query(dataTableRequest);
//			DataTableResponse tableResponse = new DataTableResponse();
//			tableResponse.setDraw(dataTableRequest.getDraw());
//			tableResponse.setRecordsTotal(queryTuple.v1);
//			tableResponse.setRecordsFiltered(queryTuple.v1);
//			tableResponse.setData(queryTuple.v2);
//			return tableResponse;
//		} catch (Exception e) {
//			log.error("", e);
//			DataTableResponse tableResponse = new DataTableResponse();
//			tableResponse.setDraw(dataTableRequest.getDraw());
//			tableResponse.setError("Error : " + e.getMessage());
//			return tableResponse;
//		}
//	}
//
//	@GetMapping(name = "sso-uri.edit", path = "/sso-uri/edit")
//	public String edit(@RequestParam(name = "id") final Long id, final ModelMap model) {
//
//		Optional<APIClient> apiClient = apiClientService.findById(id);
//		if (apiClient.isEmpty()) {
//			throw new ElementNotFoundException();
//		}
//
//		APIClient client = apiClient.get();
//		Optional<SSORedirectUri> redirectUriOpt = apiClientService.findSSORedirectUriByOwnerId(client.getId());
//
//		SSORedirectUriBind redirectUriBind = new SSORedirectUriBind();
//		redirectUriBind.setId(client.getId());
//		redirectUriBind.setClientId(client.getClientId());
//		redirectUriBind.setClientSecret(client.getClientSecret());
//		redirectUriBind.setTitle(client.getTitle());
//
//		if (redirectUriOpt.isPresent()) {
//			SSORedirectUri ssoRedirectUri = redirectUriOpt.get();
//			redirectUriBind.setItem(ssoRedirectUri.getRedirectUri());
//		} else {
//			redirectUriBind.setItem("");
//		}
//
//		model.addAttribute("redirectUriBind", redirectUriBind);
//
//		return "sso-uri/edit";
//	}
//
//	@PostMapping(name = "sso-uri.update", path = "/sso-uri/update")
//	public String update(final HttpServletRequest request, final SSORedirectUriBind redirectUriBind,
//			final ModelMap model, final RedirectAttributes redirectAttributes) {
//
//		// 目前這邊完全仰賴 parsley.js 做 client 端驗證，嚴謹的話，後端應該也要驗證
//		log.info("{}", redirectUriBind);
//
//		Optional<APIClient> apiClientOpt = apiClientService.findById(redirectUriBind.getId());
//		if (apiClientOpt.isEmpty()) {
//			redirectAttributes.addFlashAttribute("message", "此API系統不存在");
//			return "redirect:/sso-uri/index";
//		}
//
//		APIClient apiClient = apiClientOpt.get();
//
//		try {
//			SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
//
//			apiClientService.updateSSORedirectUri(apiClient, redirectUriBind, loginUser);
//
//			auditLogService.create(request, model, AuditLog.TYPE_UPDATE, apiClient.getClientId(), apiClient.getTitle(),
//					"設定API SSO URI");
//
//			redirectAttributes.addFlashAttribute("message", "儲存成功");
//		} catch (Exception e) {
//			log.error("", e);
//			redirectAttributes.addFlashAttribute("message", "儲存失敗");
//		}
//
//		return "redirect:/sso-uri/index";
//
//	}
//
//}
