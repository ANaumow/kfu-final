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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@ConditionalOnProperty(value = "module.enabled", matchIfMissing = true, havingValue = "true")
public class ModularAnnotationPostProcessor implements
        BeanPostProcessor, BeanDefinitionRegistryPostProcessor, ApplicationContextAware {

    @Setter
    private ApplicationContext applicationContext;

    private String currentModuleName;
    private String packageToScan;

    /**
     * Ищет BeanDefinition'ы, бины которых нужно запроксировать.
     * По информации в BeanDefinition определяем нужно ли в этом экземпляре
     * приложения создавать этот бин, если да заменяем этот BeanDefinition
     * на новый BeanDefinition, на основе RemoteModuleFactoryBean,
     * этот FactoryBean создаст прокси-бин
     * во время последующей инициализации контекста
     */
    @SneakyThrows
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {

        currentModuleName = applicationContext.getEnvironment().getProperty("module.name");
        packageToScan = applicationContext.getEnvironment().getProperty("module.package-to-scan");

        Objects.requireNonNull(packageToScan);
        Objects.requireNonNull(currentModuleName);

        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);

            String beanClassName = beanDefinition.getBeanClassName();
            if (beanClassName == null) {
                continue;
            }

            String beanModuleName;
            Class<?> beanClass = Class.forName(beanClassName);

            beanModuleName = defineModuleName(beanClass);

            boolean needsToProxy = needsToProxy(beanClass, beanModuleName);

            if (needsToProxy) {
                String factoryBeanName = RemoteModuleFactoryBean.class.getName();
                BeanDefinitionBuilder definitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(factoryBeanName);

                definitionBuilder.addConstructorArgValue(beanClass);
                definitionBuilder.addConstructorArgValue(beanModuleName);

                registry.removeBeanDefinition(beanDefinitionName);
                registry.registerBeanDefinition(beanDefinitionName, definitionBuilder.getBeanDefinition());
            }
        }
    }

    /**
     * Определяет нужно ли создавать прокси-бин для конкретного бина
     * @param beanClass класс бина, который проверяем
     * @param beanModuleName называние модуля в аннотации @Modular
     * @return нужно ли создавать
     */
    private boolean needsToProxy(Class<?> beanClass, String beanModuleName) {
        boolean isClientBean = beanClass.getName().startsWith(packageToScan);
        boolean isBeanForCurrentModule = Objects.equals(currentModuleName, beanModuleName);

        return isClientBean && !isBeanForCurrentModule;
    }

    /**
     * Считывает из аннотации @Modular название модуля,
     * если аннотации нет - возвращает "main", тоесть такие бины
     * должны создаваться в главном приложении
     * @param beanClass класс бина
     * @return название модуля в котором нужно запустить бин
     */
    private String defineModuleName(Class<?> beanClass) {
        String beanModuleName;
        if (beanClass.isAnnotationPresent(Modular.class)) {
            beanModuleName = beanClass.getAnnotation(Modular.class).name();
        } else {
            beanModuleName = "main";
        }
        return beanModuleName;
    }

    /**
     * Проксирование для случаев когда бин который, нужно проксировать создается
     * через другой FactoryBean, и не был обработан во время инициализии BeanDefenitionRegistry,
     * такие случаи возникают например для генерируемых репозиториев Spring Data Jpa,
     * тогда считываем класс бина из FactoryBean и проксируем если нужно
     */
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

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {

    }
}
