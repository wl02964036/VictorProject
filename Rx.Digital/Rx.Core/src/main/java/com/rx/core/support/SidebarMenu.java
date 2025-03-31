package com.rx.core.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class SidebarMenu implements Serializable {

	protected static final long serialVersionUID = 1L;

	private final String uuid;
	private final String path;
	private final String title;
	private final List<SidebarMenu> children;

	public SidebarMenu(String uuid, String path, String title) {
		this.uuid = uuid;
		this.path = path;
		this.title = title;
		this.children = new ArrayList<>();
	}

	public String getUuid() {
		return uuid;
	}

	public String getPath() {
		return path;
	}

	public String getTitle() {
		return title;
	}

	public void addChild(SidebarMenu menu) {
		this.children.add(menu);
	}

	public List<SidebarMenu> getChildren() {
		return children;
	}

}
