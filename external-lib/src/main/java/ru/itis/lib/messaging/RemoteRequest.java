package ru.itis.lib.messaging;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RemoteRequest {

    private UUID id = UUID.randomUUID();

    private String beanName;

    private String className;
    private String methodName;

    private List<String> argTypes;
    private List<String> args;

}
