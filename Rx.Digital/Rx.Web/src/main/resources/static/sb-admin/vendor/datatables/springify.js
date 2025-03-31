/**
 * spring csrf，ajax submit 的時候需要加到 header
 */
function getHeaders() {
    var csrfHeaders = {};
    csrfHeaders[$("meta[name='_csrf_header']").attr("content")] = $("meta[name='_csrf']").attr("content");
    return csrfHeaders;
}

/**
 * 將 datatable 的 params，轉成 spring binding 的參數
 */
function springify(params, settings) {
    params.columns.forEach(function (column, index) {
        params["columns[" + index + "].index"] = index;
        params["columns[" + index + "].data"] = column.data;
        params["columns[" + index + "].name"] = column.name;
        params["columns[" + index + "].searchable"] = column.searchable;
        params["columns[" + index + "].orderable"] = column.orderable;
        params["columns[" + index + "].search.regex"] = column.search.regex;
        params["columns[" + index + "].search.value"] = column.search.value;
    });
    delete params.columns;

    params.order.forEach(function (order, index) {
        params["order[" + index + "].column"] = order.column;
        params["order[" + index + "].dir"] = order.dir;
    });
    delete params.order;

    params["search.regex"] = params.search.regex;
    params["search.value"] = params.search.value;
    delete params.search;

    return params;
}
