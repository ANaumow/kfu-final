package com.example.clientapp.controller;

import com.example.clientapp.dto.ResponseDto;
import com.example.clientapp.entity.Request;
import com.example.clientapp.repo.RequestRepository;
import com.example.clientapp.service.ProductInfo;
import com.example.clientapp.service.ProductService;
import com.example.clientapp.service.SomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SomeController {

    @Autowired
    SomeService someService;

    @Autowired
    ProductService productService;

    @Autowired
    RequestRepository requestRepository;

    @GetMapping("/some")
    public ResponseDto some() {
        return someService.getResponse();
    }

    @GetMapping("/repo")
    public Request repo() {
        return requestRepository.getById(1L);
    }

    @GetMapping("/calc")
    public ResponseDto calc() {
        long start = System.nanoTime();

        ResponseDto result = someService.calc();

        long finish = System.nanoTime();

        System.out.println(finish - start);

        return result;
    }

    @GetMapping("/info")
    public ProductInfo getInfo() {
        return productService.getProductInfo();
    }


}
