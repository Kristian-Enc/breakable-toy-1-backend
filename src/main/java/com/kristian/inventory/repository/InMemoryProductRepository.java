package com.kristian.inventory.repository;

import com.kristian.inventory.model.Product;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

@Repository
public class InMemoryProductRepository implements ProductRepository {

    // This is my HashMap where I will store my data
    private final Map<Long, Product> productStorage = new HashMap<>();

    // I will set the id automatically so I decided to start with 1000
    private Long newId = 1000L;

    @Override
    public List<Product> findAll(){
        return new ArrayList<>(productStorage.values());
    }

    @Override
    public Optional<Product> findById(Long id){
        return Optional.ofNullable(productStorage.get(id));
    }

    @Override
    public Product save(Product product){
        LocalDateTime now = LocalDateTime.now();

        // Create
        if (product.getId() == null){
          product.setId(newId++);
          product.setCreatedAt(now);
        }

        product.setUpdatedAt(now);
        productStorage.put(product.getId(), product);

        return product;

    }

    @Override
    public void deleteById(Long id){
        productStorage.remove(id);
    }

}
