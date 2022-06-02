package ru.itis.lib.lib.processing;//package com.external.lib.processing;
//
//import com.external.lib.Modular;
//import com.external.lib.messaging.RemoteRequest;
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.cloud.client.ServiceInstance;
//import org.springframework.cloud.client.discovery.DiscoveryClient;
//import org.springframework.context.ApplicationStartupAware;
//import org.springframework.core.metrics.ApplicationStartup;
//import org.springframework.http.HttpEntity;
//import org.springframework.stereotype.Component;
//import org.springframework.web.client.RestTemplate;
//
//import java.lang.reflect.InvocationHandler;
//import java.lang.reflect.Proxy;
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//import java.util.Locale;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
//@Component
//@Slf4j
//public class ModulePostProcessor implements BeanPostProcessor, ApplicationStartupAware, ApplicationRunner {
//
//    @Value("${module.name:}")
//    private String appModuleName;
//
//    @Value("${module.package-to-scan:unsafe}")
//    private String applicationPackage;
//
//    @Autowired(required = false)
//    private DiscoveryClient discoveryClient;
//
//    @Autowired
//    private RestTemplate restTemplate;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    private List<String> beanNames = new ArrayList<>();
//
//    @Override
//    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
//        Class<?> beanClass = bean.getClass();
//
//        if (!beanClass.getName().startsWith(applicationPackage)) {
//            return bean;
//        }
//
//        beanNames.add(beanName);
//
//        if (beanClass.isAnnotationPresent(Modular.class)) {
//            String beanModuleName = beanClass.getAnnotation(Modular.class).name();
//
//            if (!Objects.equals(appModuleName, beanModuleName)) {
//                return createProxyBean(bean, beanName, beanClass, beanModuleName);
//            }
//        }
//
////        if (!appModuleName.equals("main")) {
////            return
////        }
//
//        return bean;
//    }
//
//    private Object createProxyBean(Object bean, String beanName, Class<?> beanClass, String beanModuleName) {
//        InvocationHandler invocationHandler;
//
//        invocationHandler = (proxy, method, args) -> {
//
//            String classNameWithPackage = bean.getClass().getName();
//            String methodName = method.getName();
//
//
//            List<String> requestArgTypes = Arrays.stream(args).map(Object::getClass).map(Class::getName).collect(Collectors.toList());
//            List<String> requestArgs = Arrays.stream(args).map(value -> {
//                try {
//                    return mapper.writeValueAsString(value);
//                } catch (JsonProcessingException e) {
//                    throw new IllegalArgumentException();
//                }
//                    }
//            ).collect(Collectors.toList());
//
//            RemoteRequest remoteRequest = new RemoteRequest();
//            remoteRequest.setClassName(classNameWithPackage);
//            remoteRequest.setArgTypes(requestArgTypes);
//            remoteRequest.setArgs(requestArgs);
//            remoteRequest.setMethodName(methodName);
//            remoteRequest.setBeanName(beanName);
//
//
//            HttpEntity<RemoteRequest> request = new HttpEntity<>(remoteRequest);
//
//            discoveryClient.getServices();
//
//            ServiceInstance instance = discoveryClient.getInstances("unknown").stream()
//                                                      .filter(i -> i.getServiceId().toLowerCase(Locale.ROOT).equals(beanModuleName.toLowerCase(Locale.ROOT)))
//                                                      .findFirst()
//                                                      .orElseThrow();
//
//            int port = instance.getPort();
//
//            String url = "http://localhost:" + port + "/rpc";
//
//            return restTemplate.postForObject(url, request, method.getReturnType());
//        };
//
//        Object proxyInstance = Proxy.newProxyInstance(beanClass.getClassLoader(),
//                beanClass.getInterfaces(),
//                invocationHandler);
//
//        return proxyInstance;
//    }
//
//    @Override
//    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
//        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
//    }
//
//    @Override
//    public void setApplicationStartup(ApplicationStartup applicationStartup) {
//    }
//
//    @Override
//    public void run(ApplicationArguments args) throws Exception {
//        log.info(beanNames.toString());
//    }
//}
