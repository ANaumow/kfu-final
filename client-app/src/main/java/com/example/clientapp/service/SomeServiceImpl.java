package com.example.clientapp.service;

import com.example.clientapp.dto.ResponseDto;
import com.example.clientapp.entity.Request;
import com.example.clientapp.repo.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.lib.Modular;

import javax.annotation.PostConstruct;
import java.time.Instant;

@Service
@Modular(name = "some-module")
public class SomeServiceImpl implements SomeService {

    @Autowired
    RequestRepository requestRepository;

    public SomeServiceImpl() {
        System.out.println("SomeServiceImpl");
    }

    @PostConstruct
    public void init() {
        System.out.println("init");
    }

    public String version() {
        return "123";
    }

    @Override
    public ResponseDto getResponse() {
        requestRepository.save(new Request(null, Instant.now().toString()));
        return new ResponseDto("data");
    }
}
