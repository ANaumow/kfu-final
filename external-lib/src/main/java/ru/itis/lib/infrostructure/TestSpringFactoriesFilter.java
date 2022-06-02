package ru.itis.lib.infrostructure;

import org.springframework.boot.autoconfigure.AutoConfigurationImportFilter;
import org.springframework.boot.autoconfigure.AutoConfigurationMetadata;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.List;

public class TestSpringFactoriesFilter implements AutoConfigurationImportFilter, EnvironmentAware {

    private final List<String> eurekaServerConfigNames = Arrays.asList(
            "org.springframework.cloud.netflix.eureka.config.EurekaClientConfigServerAutoConfiguration",
            "org.springframework.cloud.netflix.eureka.config.DiscoveryClientOptionalArgsConfiguration",
            "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration",
            "org.springframework.cloud.netflix.eureka.EurekaDiscoveryClientConfiguration",
            "org.springframework.cloud.netflix.eureka.reactive.EurekaReactiveDiscoveryClientConfiguration",
            "org.springframework.cloud.netflix.eureka.loadbalancer.LoadBalancerEurekaAutoConfiguration"
    );

    private final String eurekaClientConfigName =
            "org.springframework.cloud.netflix.eureka.EurekaClientAutoConfiguration";

    private String moduleName;

    @Override
    public void setEnvironment(Environment environment) {
        this.moduleName = environment.getProperty("module.name");
    }

    @Override
    public boolean[] match(String[] classNames, AutoConfigurationMetadata metadata) {
        boolean[] matches = new boolean[classNames.length];

        for (int i = 0; i < classNames.length; i++) {
            String className = classNames[i];
            boolean result = true;

            /*if (isEurekaClient(className)) {
                result = true;
            } else {

                if (isEurekaServer(className) && !isMainModule()) {
                    result = false;
                } *//*else if (isEurekaServer(className) && !isMainModule()) {
                    result = false;
                }*//*

            }*/

            matches[i] = result;

        }

        return matches;
    }

    private boolean isMainModule() {
        return "main".equals(moduleName);
    }

    private boolean isEurekaClient(String className) {
        return eurekaClientConfigName.equals(className);
    }

    private boolean isEurekaServer(String className) {
        return eurekaServerConfigNames.contains(className);
    }


}
