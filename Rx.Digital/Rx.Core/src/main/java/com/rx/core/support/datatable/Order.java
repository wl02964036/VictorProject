package com.rx.core.support.datatable;

import java.io.Serializable;
import java.util.StringJoiner;

public class Order implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer column;
    private String dir;

    public Order() {
        super();
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Order.class.getSimpleName() + "[", "]")
                .add("column=" + column)
                .add("dir='" + dir + "'")
                .toString();
    }

}
