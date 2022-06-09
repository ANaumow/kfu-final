package com.example.clientapp.service;

import com.example.clientapp.dto.ResponseDto;
import com.example.clientapp.entity.Request;
import com.example.clientapp.repo.RequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itis.lib.Modular;

import java.time.Instant;

@Service
@Modular(name = "some-module")
public class AppServiceImpl implements AppService {

    @Autowired
    RequestRepository requestRepository;

    public AppServiceImpl() {
        System.out.println("SomeServiceImpl");
    }

    public String version() {
        return "123";
    }

    @Override
    public ResponseDto getResponse() {
        requestRepository.save(new Request(null, Instant.now().toString()));
        return new ResponseDto("data");
    }

    @Override
    public ResponseDto calc() {

        int sum = 0;

        for (int i = 0; i < 10; i++) {
            int sum2 = 0;
            for (int i2 = 0; i2 < 100_000_000; i2++) {
                sum2 += i2;
            }
            sum += sum2;
        }

        return new ResponseDto(sum + "");
    }
}
