package com.rx.core.support.datatable;

import java.io.Serializable;
import java.util.List;
import java.util.StringJoiner;

import lombok.Data;

@Data
public class DataTableRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer draw;
	private Integer start;
	private Integer length;

	private Search search;
	private List<Column> columns;
	private List<Order> order;

	// 活動模組-activId
	private String target;

	// 多語系-langCode
	private String qryLangCode;

	public DataTableRequest() {
		super();
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}

	public Integer getStart() {
		return start;
	}

	public void setStart(Integer start) {
		this.start = start;
	}

	public Integer getLength() {
		return length;
	}

	public void setLength(Integer length) {
		this.length = length;
	}

	public Search getSearch() {
		return search;
	}

	public void setSearch(Search search) {
		this.search = search;
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}

	public List<Order> getOrder() {
		return order;
	}

	public void setOrder(List<Order> order) {
		this.order = order;
	}

	public String fetchClosure() {
		return String.format("OFFSET %d ROWS FETCH NEXT %d ROWS ONLY", getStart(), getLength());
	}

	public String orderClosure() {
		if (order == null || order.size() < 1) {
			return "";
		}
		StringBuilder buffer = new StringBuilder();
		for (Order o : order) {
			Column column = columns.get(o.getColumn());
			String dir = o.getDir();
			dir = dir.toUpperCase();
			buffer.append(column.getName()).append(" ").append(dir).append(" ,");
		}
		String closure = buffer.toString();
		return closure.substring(0, closure.length() - 1);
	}

	@Override
	public String toString() {
		return new StringJoiner(", ", DataTableRequest.class.getSimpleName() + "[", "]").add("draw=" + draw)
				.add("start=" + start).add("length=" + length).add("search=" + search).add("columns=" + columns)
				.add("order=" + order).toString();
	}

}
