package com.erplus.sync.utils;

import com.erplus.sync.mybatis.MybatisManager;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

@Slf4j
public class MyProxy implements InvocationHandler {

    private final Object target;

    public MyProxy(Object target) {
        this.target = target;
    }

    public static Object createProxy(Object target) {
        ClassLoader classLoader = target.getClass().getClassLoader();
        Class<?>[] interfaces = target.getClass().getInterfaces();
        Object proxyInstance = Proxy.newProxyInstance(classLoader, interfaces, new MyProxy(target));
        log.info("生成代理对象成功");
        return proxyInstance;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) {
        Object result;
        try {
            // 调用目标方法
            result = method.invoke(target, args);
            return result;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }
}
