//package com.systex.citizen.manage.service;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Objects;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//import org.apache.commons.lang3.StringUtils;
//import org.jooq.lambda.tuple.Tuple;
//import org.jooq.lambda.tuple.Tuple2;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.unbescape.html.HtmlEscape;
//
//import com.rx.core.bean.SystemUser;
//import com.rx.core.support.datatable.DataTableRequest;
//import com.rx.web.service.APIClientService;
//import com.systex.citizen.core.domain.*;
//import com.systex.citizen.manage.bind.APIClientBind;
//import com.systex.citizen.manage.bind.ClientRedirectUriBind;
//import com.systex.citizen.manage.bind.SSORedirectUriBind;
//import com.systex.citizen.manage.viewmodel.APIClientQueryViewModel;
//
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//@Service
//public class APIClientServiceImp implements APIClientService {
//
//    @Autowired
//    protected ScopeDao scopeDao;
//
//    @Autowired
//    protected APIClientDao apiClientDao;
//
//    @Autowired
//    protected ClientScopeDao clientScopeDao;
//
//    @Autowired
//    protected ClientRedirectUriDao clientRedirectUriDao;
//
//    @Autowired
//    protected SSORedirectUriDao ssoRedirectUriDao;
//
//    @Transactional(readOnly = true)
//    public Optional<APIClient> findById(Long id) {
//        return apiClientDao.findById(id);
//    }
//
//    @Transactional(readOnly = true)
//    public List<Scope> findAllScopes() {
//        return scopeDao.findAll();
//    }
//
//    @Transactional(readOnly = true)
//    public List<ClientScope> findClientScopeByOwnerId(Long ownerId) {
//        return clientScopeDao.findByOwnerId(ownerId);
//    }
//
//    @Transactional(readOnly = true)
//    public Tuple2<Long, List<APIClientQueryViewModel>> query(DataTableRequest dataTableRequest) {
//        Long totalCount = apiClientDao.totalCount(dataTableRequest);
//        List<APIClient> pageContent = apiClientDao.queryByPage(dataTableRequest);
//
//        List<APIClientQueryViewModel> pageViewModel = pageContent.stream()
//                .map(apiClient -> {
//                    APIClientQueryViewModel viewModel = new APIClientQueryViewModel();
//                    viewModel.setId(apiClient.getId());
//                    viewModel.setClientId(apiClient.getClientId());
//                    viewModel.setClientSecret(apiClient.getClientSecret());
//                    viewModel.setTitle(apiClient.getTitle());
//                    viewModel.setCreatedAt(apiClient.getCreatedAt());
//                    return viewModel;
//                })
//                .collect(Collectors.toList());
//
//        return Tuple.tuple(totalCount, pageViewModel);
//    }
//
//    @Transactional
//    public void createAPIClient(APIClientBind apiClientBind, SystemUser loginUser) throws Exception {
//
//        LocalDateTime current = LocalDateTime.now();
//
//        APIClient apiClient = new APIClient();
//        apiClient.setClientId(apiClientBind.getClientId());
//        apiClient.setClientSecret(apiClientBind.getClientSecret());
//        apiClient.setTitle(apiClientBind.getTitle());
//        apiClient.setDescription(Objects.toString(apiClientBind.getDescription(), ""));
//        apiClient.setContractUnit(Objects.toString(apiClientBind.getContractUnit(), ""));
//        apiClient.setContractName(Objects.toString(apiClientBind.getContractName(), ""));
//        apiClient.setContractMail(Objects.toString(apiClientBind.getContractMail(), ""));
//        apiClient.setContractPhone(Objects.toString(apiClientBind.getContractPhone(), ""));
//        apiClient.setOidcEnabled(apiClientBind.getOidcEnabled());
//        apiClient.setSsoEnabled(apiClientBind.getSsoEnabled());
//        apiClient.setMessageEnabled(apiClientBind.getMessageEnabled());
//        apiClient.setCitizenSearchEnabled(apiClientBind.getCitizenSearchEnabled());
//
//        apiClient.setCreatedBy(loginUser.getUsername());
//        apiClient.setCreatedAt(current);
//        apiClient.setUpdatedBy(loginUser.getUsername());
//        apiClient.setUpdatedAt(current);
//
//        Long id = apiClientDao.create(apiClient);
//        if (id != null) {
//            String[] scopes = StringUtils.split(apiClientBind.getScopes(), ",");
//            for (String s : scopes) {
//                ClientScope clientScope = new ClientScope();
//                clientScope.setOwnerId(id);
//                clientScope.setScope(s);
//                clientScopeDao.create(clientScope);
//            }
//        }
//
//    }
//
//    @Transactional
//    public void updateAPIClient(APIClient apiClient, APIClientBind apiClientBind, SystemUser loginUser) throws Exception {
//        LocalDateTime current = LocalDateTime.now();
//
//        apiClient.setTitle(apiClientBind.getTitle());
//        apiClient.setDescription(apiClientBind.getDescription());
//        apiClient.setContractUnit(apiClientBind.getContractUnit());
//        apiClient.setContractName(apiClientBind.getContractName());
//        apiClient.setContractMail(apiClientBind.getContractMail());
//        apiClient.setContractPhone(apiClientBind.getContractPhone());
//        apiClient.setOidcEnabled(apiClientBind.getOidcEnabled());
//        apiClient.setSsoEnabled(apiClientBind.getSsoEnabled());
//        apiClient.setMessageEnabled(apiClientBind.getMessageEnabled());
//        apiClient.setCitizenSearchEnabled(apiClientBind.getCitizenSearchEnabled());
//        apiClient.setUpdatedBy(loginUser.getUsername());
//        apiClient.setUpdatedAt(current);
//
//        boolean succeed = apiClientDao.update(apiClient);
//        if (succeed) {
//            clientScopeDao.destroyByOwnerId(apiClient.getId());
//
//            String[] scopes = StringUtils.split(apiClientBind.getScopes(), ",");
//            for (String s : scopes) {
//                ClientScope clientScope = new ClientScope();
//                clientScope.setOwnerId(apiClient.getId());
//                clientScope.setScope(s);
//                clientScopeDao.create(clientScope);
//            }
//        }
//
//    }
//
//    @Transactional(readOnly = true)
//    public List<ClientRedirectUri> findClientRedirectUriByOwnerId(Long ownerId) {
//        return clientRedirectUriDao.findByOwnerId(ownerId);
//    }
//
//    @Transactional
//    public void updateClientRedirectUri(APIClient apiClient, ClientRedirectUriBind redirectUriBind, SystemUser loginUser) throws Exception {
//        LocalDateTime current = LocalDateTime.now();
//
//        apiClient.setUpdatedBy(loginUser.getUsername());
//        apiClient.setUpdatedAt(current);
//
//        boolean succeed = apiClientDao.updateModificator(apiClient);
//        if (succeed) {
//            List<String> items = redirectUriBind.getItems();
//
//            // 字串因為安全會做 escape，這邊 uri 需要 unescape
//            List<String> uris = items.stream()
//                    .map(s -> HtmlEscape.unescapeHtml(s))
//                    .collect(Collectors.toList());
//
//            // 先刪除原來的所有 redirectURI
//            clientRedirectUriDao.destroyByOwnerId(apiClient.getId());
//
//            for (String u : uris) {
//                if (StringUtils.isBlank(u)) {
//                    continue; // 空字串不存
//                }
//
//                ClientRedirectUri redirectUri = new ClientRedirectUri();
//                redirectUri.setOwnerId(apiClient.getId());
//                redirectUri.setRedirectUri(u);
//                clientRedirectUriDao.create(redirectUri);
//            }
//        }
//
//    }
//
//    @Transactional(readOnly = true)
//    public Optional<SSORedirectUri> findSSORedirectUriByOwnerId(Long ownerId) {
//        return ssoRedirectUriDao.findByOwnerId(ownerId);
//    }
//
//    @Transactional
//    public void updateSSORedirectUri(APIClient apiClient, SSORedirectUriBind redirectUriBind, SystemUser loginUser) throws Exception {
//        LocalDateTime current = LocalDateTime.now();
//
//        apiClient.setUpdatedBy(loginUser.getUsername());
//        apiClient.setUpdatedAt(current);
//
//        boolean succeed = apiClientDao.updateModificator(apiClient);
//        if (succeed) {
//            String item = redirectUriBind.getItem();
//
//            // 先刪除原來的 redirectURI
//            ssoRedirectUriDao.destroyByOwnerId(apiClient.getId());
//
//            SSORedirectUri redirectUri = new SSORedirectUri();
//            redirectUri.setOwnerId(apiClient.getId());
//            redirectUri.setRedirectUri(HtmlEscape.unescapeHtml(item.trim()));
//            ssoRedirectUriDao.create(redirectUri);
//        }
//
//    }
//
//    @Transactional(readOnly = true)
//    public List<APIClient> findSSOAPIClients() {
//        List<SSORedirectUri> ssoRedirectUris = ssoRedirectUriDao.findAll();
//
//        return ssoRedirectUris.stream()
//                .map(ssoRedirectUri -> {
//                    Long ownerId = ssoRedirectUri.getOwnerId();
//                    Optional<APIClient> apiClientOpt = apiClientDao.findById(ownerId);
//                    if (apiClientOpt.isPresent()) {
//                        return apiClientOpt.get();
//                    } else {
//                        return null;
//                    }
//                })
//                .collect(Collectors.toList());
//    }
//
//}
