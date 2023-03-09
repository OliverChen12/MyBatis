package com.powernode.bank.utils;

import org.apache.ibatis.javassist.CannotCompileException;
import org.apache.ibatis.javassist.ClassPool;
import org.apache.ibatis.javassist.CtClass;
import org.apache.ibatis.javassist.CtMethod;

import java.lang.reflect.Method;
import java.util.Arrays;

public class GenerateDaoProxy {

    public static Object generate(Class daoInterface){

        ClassPool pool = ClassPool.getDefault();

        CtClass ctClass = pool.makeClass(daoInterface.getName() + "Proxy");
        CtClass ctInterface = pool.makeInterface(daoInterface.getName());
        ctClass.addInterface(ctInterface);
        Method[] methods = daoInterface.getDeclaredMethods();
        Arrays.stream(methods).forEach(method -> {
            try {
                StringBuilder methodCode = new StringBuilder();
                methodCode.append("public ");
                methodCode.append(method.getReturnType().getName());
                methodCode.append(" ");
                methodCode.append(method.getName());
                methodCode.append("(");
                Class<?>[] parameterTypes = method.getParameterTypes();
                for(int i = 0; i < parameterTypes.length; i++){
                    Class<?> parameterType = parameterTypes[i];
                    methodCode.append(parameterType.getName());
                    methodCode.append(" ");
                    methodCode.append("arg");
                    if(i != parameterTypes.length - 1) {
                        methodCode.append(",");
                    }
                }
                methodCode.append(")");
                methodCode.append("{");
                methodCode.append("org.apache.ibatis.session.SqlSession sqlSession = com.powernode.bank.utils.SqlSessionUtil.openSession();");


                String operationType = method.getName().substring(0,6);
                if(operationType.equals("select")){
                    methodCode.append("return (com.powernode.bank.pojo.Account)sqlSession.");
                    methodCode.append("selectOne(\"account.selectByActno\", arg);" );
                }
                else {
                    methodCode.append("return (int)sqlSession.");
                    methodCode.append("update(\"account.updateByActno\", arg);");
                }
                methodCode.append("}");
                CtMethod ctMethod = CtMethod.make(methodCode.toString(), ctClass);
                ctClass.addMethod(ctMethod);
            } catch (CannotCompileException e) {
                e.printStackTrace();
            }
        });

        Object obj = null;
        try {
            Class<?> clazz = ctClass.toClass();
            System.out.println("1");
            obj = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return obj;
    }

}
