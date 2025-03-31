package com.rx.core.support.datatable;

import java.io.Serializable;
import java.util.StringJoiner;

public class Search implements Serializable {

    private static final long serialVersionUID = 1L;

    private String value;
    private Boolean regex;

    public Search() {
        super();
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Boolean getRegex() {
        return regex;
    }

    public void setRegex(Boolean regex) {
        this.regex = regex;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Search.class.getSimpleName() + "[", "]")
                .add("value='" + value + "'")
                .add("regex=" + regex)
                .toString();
    }

}
