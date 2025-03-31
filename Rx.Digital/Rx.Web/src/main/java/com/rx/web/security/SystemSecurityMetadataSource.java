package com.rx.web.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import com.rx.core.bean.SystemMenu;
import com.rx.core.dao.RoleMenuDao;
import com.rx.core.dao.SystemMenuDao;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SystemSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

	@Autowired
	protected SystemMenuDao systemMenuDao;

	@Autowired
	protected RoleMenuDao roleMenuDao;

	/**
	 * 建立權限物件，目前只支援兩層的選單
	 */
	private Map<String, Collection<ConfigAttribute>> getMetadataSource() {
		Map<String, Collection<ConfigAttribute>> metaDataSource = new LinkedHashMap<>();

		// 找尋第一層的選單
		List<SystemMenu> menuList = systemMenuDao.findByLevel(0);

		for (SystemMenu menu : menuList) {

			// 判斷是不是目錄
			if (!menu.getPath().equals("#")) {
				String url = menu.getPath();
				List<String> roles = roleMenuDao.findByUUID(menu.getUuid()).stream()
						.map((relation) -> relation.getCode()).collect(Collectors.toList());

				Collection<ConfigAttribute> configAttributes;
				if (metaDataSource.containsKey(url)) {
					configAttributes = metaDataSource.get(url);
					roles.stream().forEach((roleName) -> {
						ConfigAttribute configAttribute = new SecurityConfig(roleName);
						configAttributes.add(configAttribute);
					});
				} else {
					configAttributes = new ArrayList<>();
					roles.stream().forEach((roleName) -> {
						ConfigAttribute configAttribute = new SecurityConfig(roleName);
						configAttributes.add(configAttribute);
					});
				}
				metaDataSource.put(url, configAttributes);
			} else {

				// 抓取第二層的選單
				List<SystemMenu> subMenus = systemMenuDao.findByParent(menu.getUuid());
				for (SystemMenu sub : subMenus) {
					String url = sub.getPath();
					List<String> roles = roleMenuDao.findByUUID(sub.getUuid()).stream()
							.map((relation) -> relation.getCode()).collect(Collectors.toList());

					Collection<ConfigAttribute> configAttributes;
					if (metaDataSource.containsKey(url)) {
						configAttributes = metaDataSource.get(url);
						roles.stream().forEach((roleName) -> {
							ConfigAttribute configAttribute = new SecurityConfig(roleName);
							configAttributes.add(configAttribute);
						});
					} else {
						configAttributes = new ArrayList<>();
						roles.stream().forEach((roleName) -> {
							ConfigAttribute configAttribute = new SecurityConfig(roleName);
							configAttributes.add(configAttribute);
						});
					}
					metaDataSource.put(url, configAttributes);
				}
			}
		}

		return metaDataSource;
	}

	/**
	 * 返回本次訪問所需要的權限，可以有多個權限。 目前沒有match的url直接返回空權限，表示可以訪問。
	 */
	@Override
	public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
		Map<String, Collection<ConfigAttribute>> metadataSource = getMetadataSource();

		FilterInvocation fi = (FilterInvocation) object;

		for (Map.Entry<String, Collection<ConfigAttribute>> entry : metadataSource.entrySet()) {
			String url = entry.getKey();

			// 如果 url match ...，回傳可以存取的ROLE
			RequestMatcher requestMatcher = new AntPathRequestMatcher(url);
			if (requestMatcher.matches(fi.getHttpRequest())) {
				return entry.getValue();
			}
		}

		return new ArrayList<>();
	}

	/**
	 * Spring Security會在啟動時校驗每個 ConfigAttribute 是否配置正确，不需要校驗直接返回null。
	 */
	@Override
	public Collection<ConfigAttribute> getAllConfigAttributes() {
		return null;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return FilterInvocation.class.isAssignableFrom(clazz);
	}

}
