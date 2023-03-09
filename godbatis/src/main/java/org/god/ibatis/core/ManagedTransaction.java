package org.god.ibatis.core;

import java.sql.Connection;

//‘›≤ª µœ÷
public class ManagedTransaction implements Transaction{
    @Override
    public void commit() {

    }

    @Override
    public void rollback() {

    }

    @Override
    public void close() {

    }

    @Override
    public void openConnection() {

    }

    @Override
    public Connection getConnection() {
        return null;
    }
}
