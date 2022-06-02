package ru.itis.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.itis.lib.messaging.RemoteRequest;
import ru.itis.lib.messaging.RemoteResponse;
import ru.itis.lib.security.JwtAuthenticationFilter;

import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Component
@Slf4j
public class ModuleProxyHandler {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private DiscoveryClient discoveryClient;

    @Autowired
    private RestTemplate restTemplate;

    @SneakyThrows
    public <T> Object invoke(Class<T> beanClass, String beanName, String beanModuleName, Method method, Object[] args) {
        String classNameWithPackage = beanClass.getName();
        String methodName = method.getName();

        List<String> requestArgTypes = new ArrayList<>();
        List<String> requestArgs = new ArrayList<>();
        if (args != null) {
            requestArgTypes = Arrays.stream(args).map(Object::getClass).map(Class::getName).collect(Collectors.toList());
            requestArgs = Arrays.stream(args).map(value -> {
                        try {
                            return mapper.writeValueAsString(value);
                        } catch (JsonProcessingException e) {
                            throw new IllegalArgumentException();
                        }
                    }
            ).collect(Collectors.toList());
        }
        RemoteRequest remoteRequest = new RemoteRequest();
        remoteRequest.setClassName(classNameWithPackage);
        remoteRequest.setArgTypes(requestArgTypes);
        remoteRequest.setArgs(requestArgs);
        remoteRequest.setMethodName(methodName);
        remoteRequest.setBeanName(beanName);

        String url = "http://" + beanModuleName.toLowerCase(Locale.ROOT) + "/rpc";

        for (Parameter parameter : method.getParameters()) {
            parameter.getType();
        }

        String token = JwtAuthenticationFilter.token;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", token);
        HttpEntity<RemoteRequest> request = new HttpEntity<>(remoteRequest, headers);
        RemoteResponse remoteResponse = restTemplate.postForObject(url, request, RemoteResponse.class);

        byte[] targetClass = remoteResponse.getTargetClass();

        ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(targetClass));

        Class<?> type = (Class<?>) objectInputStream.readObject();
        Object object = mapper.readValue(remoteResponse.getJson(), type);

        return object;
    }

}
