package com.example.clientapp.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Product implements Comparable<Product> {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private Integer cost;

    @Override
    public int compareTo(Product o) {
        return (int) (this.id - o.id);
    }
}
