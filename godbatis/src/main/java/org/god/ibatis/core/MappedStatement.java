package org.god.ibatis.core;

public class MappedStatement {

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public String getReturnType() {
        return returnType;
    }

    public void setReturnType(String returnType) {
        this.returnType = returnType;
    }

    private String sql;
    private String returnType;

    @Override
    public String toString() {
        return "MappedStatement{" +
                "sql='" + sql + '\'' +
                ", returnType='" + returnType + '\'' +
                '}';
    }

    public MappedStatement(String sql, String returnType) {
        this.sql = sql;
        this.returnType = returnType;
    }
}
