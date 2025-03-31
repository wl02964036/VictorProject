package com.rx.core.table.request;

import com.rx.core.support.datatable.DataTableRequest;

import lombok.Data;

@Data
public class RoleTableRequest extends DataTableRequest {

	private static final long serialVersionUID = 1L;

	private String queryTitle;

}
