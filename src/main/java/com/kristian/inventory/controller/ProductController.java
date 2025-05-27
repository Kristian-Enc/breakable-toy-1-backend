package com.kristian.inventory.controller;

import com.kristian.inventory.dto.InventoryMetrics;
import com.kristian.inventory.model.Product;
import com.kristian.inventory.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "http://localhost:8080")
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String secondarySortBy,
            @RequestParam(defaultValue = "asc") String secondarySortDir
    ){
        return productService.getSortedAndPaginatedProducts(
                page, size, sortBy, sortDir, secondarySortBy, secondarySortDir
        );
    }


    @PostMapping
    public Product createProduct(@RequestBody Product product) {
        return productService.createProduct(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product updatedProduct) {

        return productService.updateProduct(id, updatedProduct)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
        }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        boolean deleted = productService.deleteProduct(id);
        if (deleted) {
            return ResponseEntity.noContent().build(); // Succesfully deleted
        } else {
            return ResponseEntity.notFound().build(); // Product doesn't exist
        }
    }

    // Filter
    @GetMapping("/search")
    public List<Product> searchByName(@RequestParam String name) {
        return productService.searchByName(name);
    }

    @GetMapping("/filter")
    public List<Product> filterByCategory(@RequestParam String category) {
        return productService.filterByCategory(category);
    }

    // Stock

    @PutMapping("/{id}/outofstock")
    public ResponseEntity<Product> markOutOfStock(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOpt.get();
        product.setQuantityInStock(0);
        productService.updateProduct(id, product);

        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}/instock")
    public ResponseEntity<Product> markInStock(@PathVariable Long id) {
        Optional<Product> productOpt = productService.getProductById(id);

        if (productOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Product product = productOpt.get();
        product.setQuantityInStock(10);
        productService.updateProduct(id, product);

        return ResponseEntity.ok(product);
    }

    @GetMapping("/outofstock")
    public List<Product> getOutOfStockProducts() {
        return productService.getOutOfStockProducts();
    }


    /// ///////////////////

    @PostMapping("/bulk")
    public List<Product> createProducts(@RequestBody List<Product> products) {
        return products.stream()
                .map(productService::createProduct)
                .toList();
    }

    // DTO
    @GetMapping("/metrics")
    public InventoryMetrics getInventoryMetrics() {
        return productService.getInventoryMetrics();
    }










}
