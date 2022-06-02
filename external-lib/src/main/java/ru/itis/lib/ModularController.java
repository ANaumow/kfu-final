package ru.itis.lib;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.itis.lib.messaging.RemoteRequest;
import ru.itis.lib.messaging.RemoteResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@RestController
@Slf4j
public class ModularController {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ObjectMapper mapper;

    @PostMapping("/rpc")
    public RemoteResponse rpc(@RequestBody RemoteRequest remoteRequest) {

//        logger.log(String.format("received %s", remoteRequest.getId()));

        List<String> argTypes = remoteRequest.getArgTypes();
        List<String> argJsons = remoteRequest.getArgs();

        Class<?>[] argClasses = getBeanMethodArgTypes(argTypes);
        Object[] beadMethodArgValues = getBeanMethodArgValues(argJsons, argClasses);

        String methodName = remoteRequest.getMethodName();
        String beanName = remoteRequest.getBeanName();

        try {
            Object bean = context.getBean(beanName);

            log.info("получил запрос");

            Method method = null;
            try {
                method = bean.getClass().getMethod(methodName, argClasses);
            } catch (NoSuchMethodException | SecurityException e) {

                Class<Object>[] objects1 =
                        Arrays.stream(argClasses).map(aClass -> Object.class).toArray(Class[]::new);

                method = bean.getClass().getMethod(methodName, objects1);
            }


            Object responseObject = method.invoke(bean, beadMethodArgValues);

            responseObject = Hibernate.unproxy(responseObject);

            Class<?> responseClass = responseObject.getClass();

            byte[] bytes;

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream outputStream = new ObjectOutputStream(out);
            outputStream.writeObject(responseClass);
            outputStream.close();

            bytes = out.toByteArray();
            responseObject.getClass();

            String json = mapper.writeValueAsString(responseObject);

            RemoteResponse response = new RemoteResponse(bytes, json);

            log.info("вернул ответ");

            return response;
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

    }

    private Class<?>[] getBeanMethodArgTypes(List<String> argTypes) {
        Class<?>[] argClasses = argTypes.stream().map((String className) -> {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalArgumentException(e);
            }
        }).toArray(Class<?>[]::new);
        return argClasses;
    }

    private Object[] getBeanMethodArgValues(List<String> argJsons, Class<?>[] argClasses) {
        Object[] argObjects = new Object[argClasses.length];

        for (int i = 0; i < argJsons.size(); i++) {
            Class<?> argClass = argClasses[i];
            String argJson = argJsons.get(i);

            try {
                Object value = mapper.readValue(argJson, argClass);
                argObjects[i] = value;
            } catch (JsonProcessingException e) {
                throw new IllegalStateException(e);
            }

        }
        return argObjects;
    }

}
