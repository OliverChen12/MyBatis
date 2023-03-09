package org.god.ibatis.core;

import java.util.Map;

public class SqlSessionFactory {

    private Transaction transaction;

    private Map<String,MappedStatement> mappedStatement;

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Map<String, MappedStatement> getMappedStatement() {
        return mappedStatement;
    }

    public void setMappedStatement(Map<String, MappedStatement> mappedStatement) {
        this.mappedStatement = mappedStatement;
    }

    public SqlSession openSession(){

        transaction.openConnection();
        SqlSession sqlSession = new SqlSession(this);

        return sqlSession;
    }

    public SqlSessionFactory() {
    }

    public SqlSessionFactory(Transaction transaction, Map<String, MappedStatement> mappedStatement) {
        this.transaction = transaction;
        this.mappedStatement = mappedStatement;
    }
}
