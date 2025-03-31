package com.rx.core.support.datatable;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class DataTableResponse implements Serializable {

	private static final long serialVersionUID = 1L;

	private Integer draw = 0;
	private Long recordsTotal = 0L; // 篩選前的總資料數
	private Long recordsFiltered = 0L; // 篩選後的總資料數(jQuery DataTable內建的篩選，因本案沒用到，與recordsTotal相同。)
	private List<?> data = new LinkedList<>();
	private String error = "";

	public DataTableResponse() {
		super();
	}

	public Integer getDraw() {
		return draw;
	}

	public void setDraw(Integer draw) {
		this.draw = draw;
	}

	public Long getRecordsTotal() {
		return recordsTotal;
	}

	public void setRecordsTotal(Long recordsTotal) {
		this.recordsTotal = recordsTotal;
	}

	public Long getRecordsFiltered() {
		return recordsFiltered;
	}

	public void setRecordsFiltered(Long recordsFiltered) {
		this.recordsFiltered = recordsFiltered;
	}

	public List<?> getData() {
		return data;
	}

	public void setData(List<?> data) {
		this.data = data;
	}

	public String getError() {
		return error;
	}

	public void setError(String error) {
		this.error = error;
	}

}
