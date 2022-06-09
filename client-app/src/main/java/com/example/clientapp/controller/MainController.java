package com.example.clientapp.controller;

import com.example.clientapp.dto.ResponseDto;
import com.example.clientapp.entity.Request;
import com.example.clientapp.repo.RequestRepository;
import com.example.clientapp.service.ProductInfo;
import com.example.clientapp.service.ProductService;
import com.example.clientapp.service.AppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {

    @Autowired
    private ProductService productService;

    @GetMapping("/info")
    public ProductInfo getInfo() {
        return productService.getProductInfo();
    }

    @GetMapping("/client/calc")
    public ResponseDto calc() {
        long start = System.nanoTime();

        ResponseDto result = appService.calc();

        long finish = System.nanoTime();

        System.out.println(finish - start);

        return result;
    }

    @GetMapping("/client/purchase")
    public ResponseDto some() {
        return appService.getResponse();
    }

    @GetMapping("/client/request")
    public Request repo() {
        return requestRepository.getById(1L);
    }




    @Autowired
    AppService appService;

    @Autowired
    RequestRepository requestRepository;







}
