package com.example.clientapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.itis.lib.EnableModules;

@SpringBootApplication
@EnableModules
public class ClientAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(ClientAppApplication.class, args);
    }

}
