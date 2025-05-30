package com.kristian.inventory;

import com.kristian.inventory.controller.ProductController;
import com.kristian.inventory.model.Product;
import com.kristian.inventory.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.mockito.ArgumentMatchers.eq;



@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    private Product testProduct;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setCategory("Test Category");
        testProduct.setUnitPrice(new BigDecimal("10.00"));
        testProduct.setQuantityInStock(5);
    }

    // GET

    @Test
    void shouldReturnListOfProducts() throws Exception {
        given(productService.getFilteredSortedPaginatedProducts(
                0, 10, "name", "asc", null, "asc", null, null, null
        )).willReturn(List.of(testProduct));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Test Product"));
    }

    // POST
    @Test
    void shouldCreateProduct() throws Exception {
        Product newProduct = new Product();
        newProduct.setName("New Product");
        newProduct.setCategory("Electronics");
        newProduct.setUnitPrice(new BigDecimal("99.99"));
        newProduct.setQuantityInStock(20);

        // Simulate returned product (with ID)
        Product savedProduct = new Product();
        savedProduct.setId(2L);
        savedProduct.setName("New Product");
        savedProduct.setCategory("Electronics");
        savedProduct.setUnitPrice(new BigDecimal("99.99"));
        savedProduct.setQuantityInStock(20);

        given(productService.createProduct(any(Product.class))).willReturn(savedProduct);

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(newProduct);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("New Product"))
                .andExpect(jsonPath("$.category").value("Electronics"));
    }

    // PUT

    @Test
    void shouldUpdateProduct() throws Exception {
        Long productId = 2L;

        Product updatedProduct = new Product();
        updatedProduct.setName("Updated Product");
        updatedProduct.setCategory("Updated Category");
        updatedProduct.setUnitPrice(new BigDecimal("49.99"));
        updatedProduct.setQuantityInStock(15);

        Product savedProduct = new Product();
        savedProduct.setId(productId);
        savedProduct.setName("Updated Product");
        savedProduct.setCategory("Updated Category");
        savedProduct.setUnitPrice(new BigDecimal("49.99"));
        savedProduct.setQuantityInStock(15);

        given(productService.updateProduct(eq(productId), any(Product.class)))
                .willReturn(Optional.of(savedProduct));

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(updatedProduct);

        mockMvc.perform(put("/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(2))
                .andExpect(jsonPath("$.name").value("Updated Product"))
                .andExpect(jsonPath("$.category").value("Updated Category"));
    }




}