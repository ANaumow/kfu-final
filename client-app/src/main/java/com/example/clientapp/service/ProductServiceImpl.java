package com.example.clientapp.service;

import com.example.clientapp.entity.Product;
import com.example.clientapp.repo.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.itis.lib.Modular;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;

@Component
@RequiredArgsConstructor
@Modular(name = "some-module")
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductInfo getProductInfo() {

        List<Product> products = productRepository.findAll();

        Integer sum = products.stream().map(Product::getCost).reduce(0, Integer::sum);

        TreeMap<String, Integer> treeMap = products.stream().collect(
                (Supplier<TreeMap<String, Integer>>) TreeMap::new,
                (map, product) -> {
                    map.putIfAbsent(product.getName(), 0);
                    map.computeIfPresent(product.getName(), (name, val) -> val + 1);
                },
                TreeMap::putAll
        );

        treeMap.entrySet().forEach(s -> {
            System.out.println(s.getKey());
            System.out.println(s.getValue());
        });

        return new ProductInfo(sum, new ArrayList<>());
    }
}
