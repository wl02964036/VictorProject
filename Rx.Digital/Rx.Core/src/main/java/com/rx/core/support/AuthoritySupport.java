package com.rx.core.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.rx.core.bean.RoleUser;
import com.rx.core.bean.SystemMenu;
import com.rx.core.dao.SystemMenuDao;

public final class AuthoritySupport {

	public static List<GrantedAuthority> getAuthorities(List<RoleUser> rolesByUser) {
		List<GrantedAuthority> authorities = new ArrayList<>();
		for (RoleUser roleUser : rolesByUser) {
			authorities.add(new SimpleGrantedAuthority(roleUser.getCode()));
		}
		return authorities;
	}

	/**
	 * 回傳 允許存取的側選單，注意系統只有兩層(配合sbadmin2)
	 * 
	 * @return
	 */
	public static List<SidebarMenu> buildSidebarMenus(SystemMenuDao systemMenuDao, Set<SystemMenu> authorityPaths, boolean isAngular) {
		List<SidebarMenu> menus = new ArrayList<>();
		List<SystemMenu> rootLevels = systemMenuDao.findByLevel(0);

		// 先找出 User 可以看到的第一層的選單
		for (SystemMenu menu : rootLevels) {
			if (menu.getPath().equals("#")) {
				List<SystemMenu> children = systemMenuDao.findByParent(menu.getUuid());
				for (SystemMenu c : children) {
					if (authorityPaths.contains(c)) {
						String url = menu.getPath();
						// only for angular api
						if (isAngular && !url.equals("#")) {
							url = String.format("/manage%s", url);
						}
						if (url.endsWith("/**")) {
							url = url.replace("/**", "/index");
						}
						SidebarMenu sidebarMenu = new SidebarMenu(menu.getUuid(), url, menu.getTitle());
						menus.add(sidebarMenu);
						break;
					}
				}
			} else {
				if (authorityPaths.contains(menu)) {
					String url = menu.getPath();
					// only for angular api
					if (isAngular) {
						url = String.format("/manage%s", url);
					}
					if (url.endsWith("/**")) {
						url = url.replace("/**", "/index");
					}
					SidebarMenu sidebarMenu = new SidebarMenu(menu.getUuid(), url, menu.getTitle());
					menus.add(sidebarMenu);
				}
			}
		}

		// 從第一層再產生第二層的選單
		for (SidebarMenu top : menus) {
			Optional<SystemMenu> sysMenuOpt = systemMenuDao.findByUUID(top.getUuid());
			if (sysMenuOpt.isPresent()) {
				SystemMenu parentMenu = sysMenuOpt.get();
				List<SystemMenu> children = systemMenuDao.findByParent(parentMenu.getUuid());
				for (SystemMenu c : children) {
					if (authorityPaths.contains(c)) {
						String subUrl = c.getPath();
						// only for angular api
						if (isAngular) {
							subUrl = String.format("/manage%s", subUrl);
						}
						if (subUrl.endsWith("/**")) {
							subUrl = subUrl.replace("/**", "/index");
						}
						SidebarMenu nextMenu = new SidebarMenu(c.getUuid(), subUrl, c.getTitle());
						top.addChild(nextMenu);
					}
				}
			}
		}

		return Collections.unmodifiableList(menus);
	}

	private AuthoritySupport() {
		super();
	}

}
