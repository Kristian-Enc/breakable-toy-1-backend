package com.kristian.inventory.service;

import com.kristian.inventory.dto.InventoryMetrics;
import com.kristian.inventory.model.Product;
import com.kristian.inventory.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    public ProductServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
    }

    @Override
    public Product createProduct(Product product){
        return productRepository.save(product);
    }

    @Override
    public Optional<Product> updateProduct(Long id, Product updatedProduct) {
        Optional<Product> existingProductOpt = productRepository.findById(id);

        if (existingProductOpt.isEmpty()) {
            return Optional.empty();
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
                .collect(Collectors.toList());
    }

    @Override
    public List<Product> filterByCategory(String category) {
        return productRepository.findAll().stream()
                .filter(p -> p.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAvailable(Long id) {
        return productRepository.findById(id)
                .map(p -> p.getQuantityInStock() > 0)
                .orElse(false);
    }

    @Override
    public List<Product> getAllProducts(){
        return productRepository.findAll();
    }

    @Override
    public Optional<Product> getProductById(Long id){
        return productRepository.findById(id);
    }

    @Override
    public List<Product> getOutOfStockProducts() {
        return productRepository.findAll().stream()
                .filter(p -> p.getQuantityInStock() == 0)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryMetrics getInventoryMetrics() {
        List<Product> products = productRepository.findAll();

        int totalStock = 0;
        BigDecimal totalValue = BigDecimal.ZERO;
        BigDecimal totalPriceSum = BigDecimal.ZERO;
        int inStockCount = 0;

        Map<String, Integer> categoryToStock = new HashMap<>();
        Map<String, BigDecimal> categoryToValue = new HashMap<>();
        Map<String, BigDecimal> categoryToPriceSum = new HashMap<>();
        Map<String, Integer> categoryToInStockCount = new HashMap<>();

        for (Product product : products) {
            int qty = product.getQuantityInStock();
            BigDecimal price = product.getUnitPrice();
            BigDecimal value = price.multiply(BigDecimal.valueOf(qty));

            totalStock += qty;
            totalValue = totalValue.add(value);

            if (qty > 0) {
                totalPriceSum = totalPriceSum.add(price);
                inStockCount++;
            }

            String category = product.getCategory();

            categoryToStock.put(
                    category,
                    categoryToStock.getOrDefault(category, 0) + qty
            );

            categoryToValue.put(
                    category,
                    categoryToValue.getOrDefault(category, BigDecimal.ZERO).add(value)
            );

            if (qty > 0) {
                categoryToPriceSum.put(
                        category,
                        categoryToPriceSum.getOrDefault(category, BigDecimal.ZERO).add(price)
                );

                categoryToInStockCount.put(
                        category,
                        categoryToInStockCount.getOrDefault(category, 0) + 1
                );
            }
        }

        BigDecimal averagePrice = BigDecimal.ZERO;
        if (inStockCount > 0) {
            averagePrice = totalPriceSum.divide(BigDecimal.valueOf(inStockCount), 2, BigDecimal.ROUND_HALF_UP);
        }

        Map<String, InventoryMetrics.CategoryMetrics> byCategory = new HashMap<>();

        for (String category : categoryToStock.keySet()) {
            int catStock = categoryToStock.get(category);
            BigDecimal catValue = categoryToValue.get(category);
            BigDecimal catAveragePrice = BigDecimal.ZERO;

            if (categoryToInStockCount.containsKey(category) && categoryToInStockCount.get(category) > 0) {
                BigDecimal priceSum = categoryToPriceSum.get(category);
                int count = categoryToInStockCount.get(category);
                catAveragePrice = priceSum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
            }

            byCategory.put(category, new InventoryMetrics.CategoryMetrics(catStock, catValue, catAveragePrice));
        }

        InventoryMetrics metrics = new InventoryMetrics();
        metrics.setTotalStock(totalStock);
        metrics.setTotalValue(totalValue);
        metrics.setAveragePrice(averagePrice);
        metrics.setByCategory(byCategory);

        return metrics;
    }

    @Override
    public List<Product> getFilteredSortedPaginatedProducts(
            int page,
            int size,
            String sortBy,
            String sortDir,
            String secondarySortBy,
            String secondarySortDir,
            String name,
            String category,
            Boolean availability
    ) {
        List<Product> products = productRepository.findAll();

        if (name != null && !name.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getName().toLowerCase().contains(name.toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getCategory().equalsIgnoreCase(category))
                    .collect(Collectors.toList());
        }

        if (availability != null) {
            products = products.stream()
                    .filter(p -> availability ? p.getQuantityInStock() > 0 : p.getQuantityInStock() == 0)
                    .collect(Collectors.toList());
        }

        Comparator<Product> primary = getComparator(sortBy, sortDir);
        Comparator<Product> secondary = (secondarySortBy != null && !secondarySortBy.isEmpty())
                ? getComparator(secondarySortBy, secondarySortDir)
                : Comparator.comparing(Product::getId);

        products.sort(primary.thenComparing(secondary));

        int start = page * size;
        int end = Math.min(start + size, products.size());

        if (start >= products.size()) {
            return List.of();
        }

        return products.subList(start, end);
    }

    // ------------------- JUST FOR TESTING -------------------->>
    @Override
    public long countFilteredProducts(String name, String category, Boolean availability) {
        return productRepository.findAll().stream()
                .filter(p -> name == null || p.getName().toLowerCase().contains(name.toLowerCase()))
                .filter(p -> category == null || category.isEmpty() || p.getCategory().equalsIgnoreCase(category))
                .filter(p -> availability == null || (availability ? p.getQuantityInStock() > 0 : p.getQuantityInStock() == 0))
                .count();
    }


    private Comparator<Product> getComparator(String field, String dir) {
        Comparator<Product> comparator;

        switch (field.toLowerCase()) {
            case "name" -> comparator = Comparator.comparing(p -> p.getName().toLowerCase());
            case "category" -> comparator = Comparator.comparing(p -> p.getCategory().toLowerCase());
            case "price" -> comparator = Comparator.comparing(Product::getUnitPrice);
            case "stock" -> comparator = Comparator.comparing(Product::getQuantityInStock);
            case "expiration" -> comparator = Comparator.comparing(
                    Product::getExpirationDate,
                    Comparator.nullsLast(Comparator.naturalOrder())
            );
            case "availability" -> comparator = Comparator.comparingInt(p -> p.getQuantityInStock() > 0 ? 1 : 0);
            default -> comparator = Comparator.comparing(Product::getId);
        }

        return dir.equalsIgnoreCase("desc") ? comparator.reversed() : comparator;
    }
}
