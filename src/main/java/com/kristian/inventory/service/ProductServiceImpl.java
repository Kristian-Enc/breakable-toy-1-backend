package com.kristian.inventory.service;

import com.kristian.inventory.model.Product;
import com.kristian.inventory.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    // Required
    @Override
    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingProductOpt = productRepository.findById(id);

        if (existingProductOpt.isEmpty()) {
            return Optional.empty(); // Nothing to update
        }

        Product existingProduct = existingProductOpt.get();

        updatedProduct.setId(id);
        updatedProduct.setCreatedAt(existingProduct.getCreatedAt());
        updatedProduct.setUpdatedAt(java.time.LocalDateTime.now());

        Product savedProduct = productRepository.save(updatedProduct);
        return Optional.of(savedProduct);
    }

    @Override
    public boolean deleteProduct(Long id){
        Optional<Product> existingProduct = productRepository.findById(id);

        if (existingProduct.isEmpty()) {
            return false;
        }

        productRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Product> searchByName(String name) {
        return productRepository.findAll().stream()
                .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                .toList();
    }

    @Override
    public List<Product> filterByCategory(String category) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .toList();
    }

    @Override
    public boolean isAvailable(Long id) {
        return productRepository.findById(id)
                .map(p -> p.getQuantityInStock() > 0)
                .orElse(false);
    }

    // Not required but just in case
    @Override
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }
}
