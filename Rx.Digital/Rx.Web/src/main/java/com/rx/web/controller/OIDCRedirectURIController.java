//package com.rx.web.controller;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
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
//public class OIDCRedirectURIController {
//
//    @Autowired
//    protected APIClientServiceImp apiClientService;
//
//    @Autowired
//    protected AuditLogService auditLogService;
//
//    public OIDCRedirectURIController() {
//        super();
//    }
//
//    @GetMapping(name = "oidc-uri.index", path = "/oidc-uri/index")
//    public String index(final ModelMap model) {
//        return "oidc-uri/index";
//    }
//
//    @PostMapping(name = "oidc-uri.query", path = "/oidc-uri/query", produces = MediaType.APPLICATION_JSON_VALUE)
//    @ResponseBody
//    public DataTableResponse query(final DataTableRequest dataTableRequest) {
//        try {
//            Tuple2<Long, List<APIClientQueryViewModel>> queryTuple = apiClientService.query(dataTableRequest);
//            DataTableResponse tableResponse = new DataTableResponse();
//            tableResponse.setDraw(dataTableRequest.getDraw());
//            tableResponse.setRecordsTotal(queryTuple.v1);
//            tableResponse.setRecordsFiltered(queryTuple.v1);
//            tableResponse.setData(queryTuple.v2);
//            return tableResponse;
//        } catch (Exception e) {
//            log.error("", e);
//            DataTableResponse tableResponse = new DataTableResponse();
//            tableResponse.setDraw(dataTableRequest.getDraw());
//            tableResponse.setError("Error : " + e.getMessage());
//            return tableResponse;
//        }
//    }
//
//    @GetMapping(name = "oidc-uri.edit", path = "/oidc-uri/edit")
//    public String edit(@RequestParam(name = "id") final Long id,
//                       final ModelMap model) {
//        Optional<APIClient> apiClient = apiClientService.findById(id);
//        if (apiClient.isEmpty()) {
//            throw new ElementNotFoundException();
//        }
//
//        APIClient client = apiClient.get();
//
//        List<ClientRedirectUri> redirectUris = apiClientService.findClientRedirectUriByOwnerId(client.getId());
//        List<String> items = redirectUris.stream()
//                                         .map(crURI -> crURI.getRedirectUri())
//                                         .collect(Collectors.toList());
//
//        ClientRedirectUriBind redirectUriBind = new ClientRedirectUriBind();
//        redirectUriBind.setId(client.getId());
//        redirectUriBind.setClientId(client.getClientId());
//        redirectUriBind.setClientSecret(client.getClientSecret());
//        redirectUriBind.setTitle(client.getTitle());
//        redirectUriBind.setItems(items);
//
//        model.addAttribute("redirectUriBind", redirectUriBind);
//
//        return "oidc-uri/edit";
//    }
//
//    @PostMapping(name = "oidc-uri.update", path = "/oidc-uri/update")
//    public String update(
//            final HttpServletRequest request,
//            final ClientRedirectUriBind redirectUriBind,
//            final ModelMap model,
//            final RedirectAttributes redirectAttributes) {
//
//        // 目前這邊完全仰賴 parsley.js 做 client 端驗證，嚴謹的話，後端應該也要驗證
//        log.info("{}", redirectUriBind);
//
//        Optional<APIClient> apiClientOpt = apiClientService.findById(redirectUriBind.getId());
//        if (apiClientOpt.isEmpty()) {
//            redirectAttributes.addFlashAttribute("message", "此API系統不存在");
//            return "redirect:/oidc-uri/index";
//        }
//
//        APIClient apiClient = apiClientOpt.get();
//
//        try {
//            SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
//
//            apiClientService.updateClientRedirectUri(apiClient, redirectUriBind, loginUser);
//
//            auditLogService.create(request, model,
//                    AuditLog.TYPE_UPDATE, apiClient.getClientId(), apiClient.getTitle(), "設定API重導向URI");
//
//            redirectAttributes.addFlashAttribute("message", "儲存成功");
//        } catch (Exception e) {
//            log.error("", e);
//            redirectAttributes.addFlashAttribute("message", "儲存失敗");
//        }
//
//        return "redirect:/oidc-uri/index";
//    }
//
//}
