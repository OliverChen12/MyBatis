package org.god.ibatis.core;

import java.lang.reflect.Method;
import java.sql.*;

public class SqlSession {
    private SqlSessionFactory factory;

    public SqlSession(SqlSessionFactory factory) {
        this.factory = factory;
    }

    public int insert(String sqlID, Object pojo){
        int count = 0;
        try {
            Connection connection = factory.getTransaction().getConnection();
            String godbatisSql = factory.getMappedStatement().get(sqlID).getSql();
            String sql = godbatisSql.replaceAll("#\\{[a-zA-Z0-9_$]*}","?");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            //insert into t_act values (#{id},#{actno},#{balance})
            int fromIndex = 0;
            int index = 1;
            while(true){
                int jingIndex = godbatisSql.indexOf("#", fromIndex);
                if(jingIndex < 0){
                    break;
                }
                int youKouHaoIndex = godbatisSql.indexOf("}", fromIndex);
                String propertyName = godbatisSql.substring(jingIndex + 2, youKouHaoIndex).trim();
                fromIndex = youKouHaoIndex + 1;
                String getMethodName = "get" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                Method getMethod = pojo.getClass().getDeclaredMethod(getMethodName);
                Object propertyValue = getMethod.invoke(pojo);
                preparedStatement.setString(index,propertyValue.toString());
                index++;
            }


            count = preparedStatement.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return count;
    }

    public Object selectOne(String sqlId, Object param){
        Object obj = null;

        try {
            Connection connection = factory.getTransaction().getConnection();
            MappedStatement mappedStatement = factory.getMappedStatement().get(sqlId);
            //select * from t_user where id = #{id}
            String godbatisSql = mappedStatement.getSql();
            String sql = godbatisSql.replaceAll("#\\{[a-zA-Z0-9_$]*}","?");
            PreparedStatement preparedStatement = connection.prepareStatement(sql);

            preparedStatement.setString(1, param.toString());
            ResultSet rs = preparedStatement.executeQuery();
            String resultType = mappedStatement.getReturnType();


            if (rs.next()) {
                Class<?> resultTypeClass = Class.forName(resultType);
                obj = resultTypeClass.newInstance();

                ResultSetMetaData rsMetaData = rs.getMetaData();
                int columnCount = rsMetaData.getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String propertyName = rsMetaData.getColumnName(i);
                    String setMethodName = "set" + propertyName.toUpperCase().charAt(0) + propertyName.substring(1);
                    Method setMethod = resultTypeClass.getDeclaredMethod(setMethodName, String.class);
                    setMethod.invoke(obj, rs.getString(propertyName));
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

    public void commit(){
        factory.getTransaction().commit();
    }
    public void rollback() {
        factory.getTransaction().rollback();
    }
    public void close(){
        factory.getTransaction().close();
    }

}
