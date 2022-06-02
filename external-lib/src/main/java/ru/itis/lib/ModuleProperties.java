package ru.itis.lib;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "module")
@Getter
@Setter
public class ModuleProperties {
    private String name;
    private String packageToScan;
}
