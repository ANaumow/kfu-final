package com.example.clientapp.service;


import com.example.clientapp.entity.Product;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class MainService implements InitializingBean {


    @Autowired
    MainService someService;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        someService.initDb();
        System.out.println("init");
    }

    @Transactional
    public void initDb() {
//        for (int i = 0; i < 10000; i++) {
//            int typeId = ThreadLocalRandom.current().nextInt(30);
//            int typeSum = typeId * 100;
//            entityManager.persist(new Product(null, "product-" + typeId, typeSum));
//        }
    }

}
