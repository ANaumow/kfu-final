package ru.itis.lib.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
@Setter
@Getter
public class JwtProperties {

    private String secret;

}
