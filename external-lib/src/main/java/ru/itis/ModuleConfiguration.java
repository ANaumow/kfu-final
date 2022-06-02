package ru.itis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cloud.client.loadbalancer.LoadBalancerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;

@Configuration
@ComponentScan
@ConfigurationPropertiesScan
@Slf4j
public class ModuleConfiguration {


    @Bean
    public RestTemplate restTemplate(LoadBalancerInterceptor interceptor, RestTemplateBuilder builder) {
        System.out.println("rest template");

        ClientHttpRequestInterceptor interceptor2 = (request, body, execution) -> {
            log.info(request.getURI().toString());
            return execution.execute(request, body);
        };

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(interceptor);
        restTemplate.getInterceptors().add(interceptor2);

        return restTemplate;
    }


}
