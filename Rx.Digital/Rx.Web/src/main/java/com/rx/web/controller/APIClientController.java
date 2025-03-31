//package com.rx.web.controller;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.commons.lang3.StringUtils;
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
//import com.systex.citizen.core.domain.*;
//import com.systex.citizen.core.support.IdentifierSupport;
//import com.systex.citizen.manage.bind.APIClientBind;
//import com.systex.citizen.manage.service.APIClientService;
//import com.systex.citizen.manage.viewmodel.APIClientQueryViewModel;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Controller
//public class APIClientController {
//
//    @Autowired
//    protected APIClientServiceImp apiClientService;
//
//    @Autowired
//    protected AuditLogService auditLogService;
//
//    public APIClientController() {
//        super();
//    }
//
//    @GetMapping(name = "api-client.index", path = "/api-client/index")
//    public String index(final ModelMap model) {
//        return "api-client/index";
//    }
//
//    @PostMapping(name = "api-client.query", path = "/api-client/query", produces = MediaType.APPLICATION_JSON_VALUE)
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
//    @GetMapping(name = "api-client.new", path = "/api-client/new")
//    public String newApiClient(final ModelMap model) {
//        List<Scope> scopes = apiClientService.findAllScopes();
//
//        model.addAttribute("scopes", scopes);
//        model.addAttribute("apiClientBind", new APIClientBind());
//
//        return "api-client/new";
//    }
//
//    @PostMapping(name = "api-client.create", path = "/api-client/create")
//    public String create(final HttpServletRequest request,
//                         final APIClientBind apiClientBind,
//                         final ModelMap model,
//                         final RedirectAttributes redirectAttributes) {
//
//        String clientId = IdentifierSupport.clientId();
//        apiClientBind.setClientId(clientId);
//        apiClientBind.setClientSecret(IdentifierSupport.clientSecret());
//
//        try {
//            SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
//            apiClientService.createAPIClient(apiClientBind, loginUser);
//            auditLogService.create(request, model,
//                    AuditLog.TYPE_CREATE, clientId, apiClientBind.getTitle(), "");
//
//            redirectAttributes.addFlashAttribute("message", "儲存成功");
//        } catch (Exception e) {
//            log.error("", e);
//            redirectAttributes.addFlashAttribute("message", "儲存失敗");
//        }
//
//        return "redirect:/api-client/index";
//    }
//
//    @GetMapping(name = "api-client.edit", path = "/api-client/edit")
//    public String edit(@RequestParam(name = "id") final Long id,
//                       final ModelMap model) {
//        Optional<APIClient> apiClient = apiClientService.findById(id);
//        if (apiClient.isEmpty()) {
//            throw new ElementNotFoundException();
//        }
//
//        APIClient client = apiClient.get();
//
//        APIClientBind apiClientBind = new APIClientBind();
//        apiClientBind.setId(client.getId());
//        apiClientBind.setClientId(client.getClientId());
//        apiClientBind.setClientSecret(client.getClientSecret());
//        apiClientBind.setTitle(client.getTitle());
//        apiClientBind.setDescription(client.getDescription());
//        apiClientBind.setContractUnit(client.getContractUnit());
//        apiClientBind.setContractName(client.getContractName());
//        apiClientBind.setContractMail(client.getContractMail());
//        apiClientBind.setContractPhone(client.getContractPhone());
//        apiClientBind.setOidcEnabled(client.getOidcEnabled());
//        apiClientBind.setSsoEnabled(client.getSsoEnabled());
//        apiClientBind.setMessageEnabled(client.getMessageEnabled());
//        apiClientBind.setCitizenSearchEnabled(client.getCitizenSearchEnabled());
//
//        // 取出這個 client 勾選的 scope
//        List<ClientScope> clientScopes = apiClientService.findClientScopeByOwnerId(id);
//        List<String> ownedScopes = clientScopes.stream()
//                .map(clientScope -> clientScope.getScope())
//                .collect(Collectors.toList());
//
//        apiClientBind.setScopes(StringUtils.join(ownedScopes, ","));
//
//        List<Scope> scopes = apiClientService.findAllScopes();
//
//        model.addAttribute("scopes", scopes);
//        model.addAttribute("apiClientBind", apiClientBind);
//
//        return "api-client/edit";
//    }
//
//    @PostMapping(name = "api-client.update", path = "/api-client/update")
//    public String update(
//            final HttpServletRequest request,
//            final APIClientBind apiClientBind,
//            final ModelMap model,
//            final RedirectAttributes redirectAttributes) {
//
//        // 目前這邊完全仰賴 parsley.js 做 client 端驗證，嚴謹的話，後端應該也要驗證
//        log.info("{}", apiClientBind);
//
//        Optional<APIClient> apiClientOpt = apiClientService.findById(apiClientBind.getId());
//        if (apiClientOpt.isEmpty()) {
//            redirectAttributes.addFlashAttribute("message", "此API系統不存在");
//            return "redirect:/api-client/index";
//        }
//
//        APIClient apiClient = apiClientOpt.get();
//
//        try {
//            SystemUser loginUser = (SystemUser) model.getAttribute("loginUser");
//            apiClientService.updateAPIClient(apiClient, apiClientBind, loginUser);
//
//            auditLogService.create(request, model,
//                    AuditLog.TYPE_UPDATE, apiClient.getClientId(), apiClient.getTitle(), "");
//
//            redirectAttributes.addFlashAttribute("message", "儲存成功");
//        } catch (Exception e) {
//            log.error("", e);
//            redirectAttributes.addFlashAttribute("message", "儲存失敗");
//        }
//
//        return "redirect:/api-client/index";
//    }
//
//}