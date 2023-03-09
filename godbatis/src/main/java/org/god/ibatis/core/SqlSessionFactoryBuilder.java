package org.god.ibatis.core;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.god.ibatis.utils.Resources;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlSessionFactoryBuilder {
    public SqlSessionFactoryBuilder() {}

    public SqlSessionFactory build(InputStream in){
        SqlSessionFactory factory = null;
        try {
            SAXReader reader = new SAXReader();
            Document document = reader.read(in);
            Element environments = (Element) document.selectSingleNode("/configuration/environments");
            String defaultId = environments.attributeValue("default");
            Element environment = (Element) document.selectSingleNode("/configuration/environments/environment[@id='" + defaultId + "']");
            Element transactionElt = environment.element("transactionManager");
            Element dataSourceElt = environment.element("dataSource");
            List<String> sqlMapperXMLPathList = new ArrayList<>();
            List<Node> nodes = document.selectNodes("//mapper");
            nodes.forEach(node -> {
                Element mapper = (Element) node;
                String resource = mapper.attributeValue("resource");
                sqlMapperXMLPathList.add(resource);
            });

            DataSource dataSource = getDataSource(dataSourceElt);
            Transaction transaction = getTransaction(transactionElt,dataSource);

            Map<String,MappedStatement> mappedStatement = getMappedStatements(sqlMapperXMLPathList);

            factory = new SqlSessionFactory(transaction,mappedStatement);



        } catch (Exception e) {
            e.printStackTrace();
        }
        return factory ;
    }

    private Map<String, MappedStatement> getMappedStatements(List<String> sqlMapperXMLPathList) {

        Map<String,MappedStatement> mappedStatements = new HashMap<>();
        sqlMapperXMLPathList.forEach(sqlMapperXMLPath -> {
            try {
                SAXReader reader = new SAXReader();
                Document document = reader.read(Resources.getResourceAsStream(sqlMapperXMLPath));
                Element mapper = (Element) document.selectSingleNode("mapper");
                String namespace = mapper.attributeValue("namespace");
                List<Element> elements = mapper.elements();
                elements.forEach(element -> {
                    String id = element.attributeValue("id");
                    String sqlID = namespace + "." + id;
                    String resultType = element.attributeValue("resultType");
                    String sql = element.getTextTrim();
                    MappedStatement mappedStatement = new MappedStatement(sql, resultType);

                    mappedStatements.put(sqlID,mappedStatement);
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return mappedStatements;
    }

    private DataSource getDataSource(Element dataSourceElt){

        Map<String,String> map = new HashMap<>();
        List<Element> propertyElts = dataSourceElt.elements("property");
        propertyElts.forEach(propertyElt ->{
            String name = propertyElt.attributeValue("name");
            String value = propertyElt.attributeValue("value");
            map.put(name, value);
        });


        DataSource dataSource = null;
        String type = dataSourceElt.attributeValue("type").trim().toUpperCase();
        if (Const.UN_POOLED_DATASOURCE.equals(type)) {
            dataSource = new UnPooledDataSource(map.get("driver"), map.get("url"), map.get("username"), map.get("password"));
        }
        if (Const.POOLED_DATASOURCE.equals(type)) {
            dataSource = new PooledDataSource();
        }
        if (Const.JNDI_DATASOURCE.equals(type)) {
            dataSource = new JNDIDataSource();
        }


        return dataSource;
    }
    private Transaction getTransaction(Element transactionElt, DataSource dataSource){
        Transaction transaction = null;
        String type = transactionElt.attributeValue("type").trim().toUpperCase();
        if (Const.JDBC_TRANSACTION.equals(type)) {
            transaction = new JdbcTransaction(dataSource,false);
        }
        if (Const.MANAGED_TRANSACTION.equals(type)) {
            transaction = new ManagedTransaction();
        }


        return transaction;
    }
}
