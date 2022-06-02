package ru.itis.lib.lib.processing;//package com.external.lib.processing;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
//import org.springframework.stereotype.Component;
//
//import java.util.HashMap;
//import java.util.Map;
//
//@Component
//public class BeanDefinitionRegistryPostProcessorImpl implements BeanDefinitionRegistryPostProcessor {
//
//    @Override
//    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
//
//        Map<String, BeanDefinition> stringBeanDefinitionMap = new HashMap<>();
//
//        for (String beanDefinitionName : registry.getBeanDefinitionNames()) {
//            stringBeanDefinitionMap.put(beanDefinitionName, registry.getBeanDefinition(beanDefinitionName));
//        }
//
////        registry.re
//
////        stringBeanDefinitionMap.forEach((s, beanDefinition) -> {
////
////            beanDefinition.get
////
////        });
//
//    }
//
//    @Override
//    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
//    }
//}
