package com.example.clientapp.repo;

import com.example.clientapp.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.itis.lib.Modular;

@Modular(name = "product_module")
public interface ProductRepository extends JpaRepository<Product, Long> {
}
