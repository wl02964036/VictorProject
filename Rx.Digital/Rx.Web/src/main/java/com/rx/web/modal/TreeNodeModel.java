package com.rx.web.modal;

import java.io.Serializable;
import java.util.Map;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class TreeNodeModel implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final String ROOT_ID_SYMBOL = "#";

	private String id;
	private String text;
	private String icon;
	private String data;
	private Map<String, Boolean> state; // 節點狀態, see https://github.com/vakata/jstree#populating-the-tree-using-ajax
	private Map<String, String> li_attr; // attributes for the generated LI node
	private Map<String, String> a_attr; // object of values which will be used to add HTML attributes on the resulting A
										// node.
	private Object children; // true 表示還有後續節點，或是 List<TreeNodeViewModel> 不使用ajax load
}
