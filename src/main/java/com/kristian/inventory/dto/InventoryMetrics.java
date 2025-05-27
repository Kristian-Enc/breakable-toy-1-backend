package com.kristian.inventory.dto;

import java.math.BigDecimal;
import java.util.Map;

public class InventoryMetrics {

    private int totalStock;
    private BigDecimal totalValue;
    private BigDecimal averagePrice;
    private Map<String, CategoryMetrics> byCategory;

    public static class CategoryMetrics {
        private int totalStock;
        private BigDecimal totalValue;
        private BigDecimal averagePrice;

        public CategoryMetrics(int totalStock, BigDecimal totalValue, BigDecimal averagePrice) {
            this.totalStock = totalStock;
            this.totalValue = totalValue;
            this.averagePrice = averagePrice;
        }

        public int getTotalStock() {
            return totalStock;
        }

        public BigDecimal getTotalValue() {
            return totalValue;
        }

        public BigDecimal getAveragePrice() {
            return averagePrice;
        }

    }

    public int getTotalStock() {
        return totalStock;
    }

    public void setTotalStock(int totalStock) {
        this.totalStock = totalStock;
    }

    public BigDecimal getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(BigDecimal totalValue) {
        this.totalValue = totalValue;
    }

    public BigDecimal getAveragePrice() {
        return averagePrice;
    }

    public void setAveragePrice(BigDecimal averagePrice) {
        this.averagePrice = averagePrice;
    }

    public Map<String, CategoryMetrics> getByCategory() {
        return byCategory;
    }

    public void setByCategory(Map<String, CategoryMetrics> byCategory) {
        this.byCategory = byCategory;
    }

}
