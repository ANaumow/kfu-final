package com.example.clientapp.service;

import com.example.clientapp.entity.Product;
import com.example.clientapp.repo.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.itis.lib.Modular;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

@Component
@Modular(name = "product-module")
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductInfo getProductInfo() {
        List<Product> products = productRepository.findAll();

        Integer sum = products.stream()
                              .map(Product::getCost)
                              .reduce(0, Integer::sum);

        Map<String, Long> treeMap = products.stream()
                                            .collect(Collectors.groupingBy(Product::getName, Collectors.counting()));

        List<String> top = treeMap.entrySet().stream()
                                  .sorted(Entry.comparingByValue())
                                  .map(Entry::getKey)
                                  .collect(Collectors.toList());

        return new ProductInfo(sum, top);
    }
}
