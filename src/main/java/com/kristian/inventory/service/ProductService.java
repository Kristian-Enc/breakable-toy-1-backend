package com.kristian.inventory.service;

import com.kristian.inventory.dto.InventoryMetrics;
import com.kristian.inventory.model.Product;

import java.util.List;
import java.util.Optional;


public interface ProductService {
    // Required
    Product createProduct(Product product);

    Optional<Product> updateProduct(Long id, Product product);

    boolean deleteProduct(Long id);

    List<Product> searchByName(String name);

    List<Product> filterByCategory(String category);

    List<Product> getOutOfStockProducts();

    boolean isAvailable(Long id);

    List<Product> getFilteredSortedPaginatedProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String secondarySortBy,
            String secondarySortDir,
            String name,
            String category,
            Boolean availability
    );


    // Not required but just in case
    List<Product> getAllProducts();

    Optional<Product> getProductById(Long id);

    // DTO
    InventoryMetrics getInventoryMetrics();


}
