package org.god.ibatis.core;

import java.sql.Connection;

public interface Transaction {

    void commit();
    void rollback();
    void close();

    void openConnection();

    Connection getConnection();



}
