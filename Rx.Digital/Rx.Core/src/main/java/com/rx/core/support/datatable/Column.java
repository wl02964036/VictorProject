package com.rx.core.support.datatable;

import java.io.Serializable;
import java.util.StringJoiner;

public class Column implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer index;
    private String name;
    private String data;
    private Boolean searchable;
    private Boolean orderable;
    private Search search;

    public Column() {
        super();
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Boolean getSearchable() {
        return searchable;
    }

    public void setSearchable(Boolean searchable) {
        this.searchable = searchable;
    }

    public Boolean getOrderable() {
        return orderable;
    }

    public void setOrderable(Boolean orderable) {
        this.orderable = orderable;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Column.class.getSimpleName() + "[", "]")
                .add("index=" + index)
                .add("name='" + name + "'")
                .add("data='" + data + "'")
                .add("searchable=" + searchable)
                .add("orderable=" + orderable)
                .add("search=" + search)
                .toString();
    }

}
