package ru.itis.lib;

import lombok.Setter;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RemoteModuleFactoryBean implements FactoryBean<Object>, BeanNameAware, ApplicationContextAware {

    private final Class<?> beanClass;
    private final String beanModuleName;

    @Setter
    private String beanName;

    @Setter
    private ApplicationContext applicationContext;

    public RemoteModuleFactoryBean(Class<?> beanClass, String beanModuleName) {
        this.beanClass = beanClass;
        this.beanModuleName = beanModuleName;
    }

    @Override
    public Object getObject() {
        ModuleProxyHandler proxyHandler = applicationContext.getBean(ModuleProxyHandler.class);
        InvocationHandler invocationHandler = (proxy, method, args) -> proxyHandler.invoke(beanClass, beanName,
                                           beanModuleName, method, args);

        List<Class<?>> interfaces = Arrays.stream(beanClass.getInterfaces()).collect(Collectors.toList());

        if (beanClass.isInterface()) {
            interfaces.add(beanClass);
        }

        Object proxyInstance = Proxy.newProxyInstance(beanClass.getClassLoader(),
                interfaces.toArray(new Class[0]),
                invocationHandler);

        return proxyInstance;
    }

    @Override
    public Class<?> getObjectType() {
        return this.beanClass;
    }
}
