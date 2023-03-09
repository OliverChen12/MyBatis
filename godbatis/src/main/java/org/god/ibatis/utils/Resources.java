package org.god.ibatis.utils;

import java.io.InputStream;

public class Resources {
    private Resources(){}

    public static InputStream getResourceAsStream(String resource){
        return ClassLoader.getSystemClassLoader().getResourceAsStream(resource);
    }





}
