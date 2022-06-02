package ru.itis.lib;

import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

@Component
public class ModularAnnotationPostProcessor implements
        BeanPostProcessor, BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    private String appModuleName;
    private String applicationPackage;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        if (bean instanceof RemoteModuleFactoryBean || !(bean instanceof FactoryBean)) {
            return bean;
        }

        FactoryBean<?> factoryBean = (FactoryBean<?>) bean;
        Class<?> beanClass = factoryBean.getObjectType();
        if (beanClass == null) {
            return bean;
        }

        String beanModuleName = defineModuleName(beanClass);
        if (!needsToProxy(beanClass, beanModuleName)) {
            return bean;
        }

        RemoteModuleFactoryBean remoteModuleFactoryBean = new RemoteModuleFactoryBean(beanClass, beanModuleName);

        remoteModuleFactoryBean.setBeanName(beanName);
        remoteModuleFactoryBean.setApplicationContext(applicationContext);

        return remoteModuleFactoryBean;
    }

    @SneakyThrows
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        appModuleName = applicationContext.getEnvironment().getProperty("module.name");
        applicationPackage = applicationContext.getEnvironment().getProperty("module.package-to-scan");

        Objects.requireNonNull(applicationPackage);
        Objects.requireNonNull(appModuleName);

        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);

            if (beanDefinitionName.toLowerCase(Locale.ROOT).equals("requestrepository")) {
                System.out.println();
            }

            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName == null) {
                continue;
            }

            String beanModuleName;
            Class<?> beanClass = Class.forName(beanClassName);

            beanModuleName = defineModuleName(beanClass);

            boolean needsToProxy = needsToProxy(beanClass, beanModuleName);

            if (!needsToProxy) {
                continue;
            }
            String factoryBeanName = RemoteModuleFactoryBean.class.getName();
            BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(factoryBeanName);

            definitionBuilder.addConstructorArgValue(beanClass);
            definitionBuilder.addConstructorArgValue(beanModuleName);

            registry.removeBeanDefinition(beanDefinitionName);
            registry.registerBeanDefinition(beanDefinitionName, definitionBuilder.getBeanDefinition());
        }
    }

    private boolean needsToProxy(Class<?> beanClass, String beanModuleName) {
        boolean isClientBean = beanClass.getName().startsWith(applicationPackage);
        boolean isBeanForCurrentModule = Objects.equals(appModuleName, beanModuleName);

        return isClientBean && !isBeanForCurrentModule;
    }

    private String defineModuleName(Class<?> beanClass) {
        String beanModuleName;
        if (beanClass.isAnnotationPresent(Modular.class)) {
            beanModuleName = beanClass.getAnnotation(Modular.class).name();
        } else {
            beanModuleName = "main";
        }
        return beanModuleName;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
