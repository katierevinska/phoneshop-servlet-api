package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public class Product implements Cloneable, Serializable {
    private Long id;
    private String code;
    private String description;
    /**
     * null means there is no price because the product is outdated or new
     */
    private BigDecimal price;
    /**
     * can be null if the price is null
     */
    private Currency currency;
    private int stock;
    private String imageUrl;
    private transient ArrayList<PriceHistoryInfo> priceHistory;

    public Product() {
    }

    public Product(
            Long id, String code, String description, BigDecimal price,
            Currency currency, int stock, String imageUrl
    ) {
        this(code, description, price, currency, stock, imageUrl);
        this.id = id;
    }

    public Product(String code, String description, BigDecimal price,
                   Currency currency, int stock, String imageUrl
    ) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
        priceHistory = new ArrayList<>();
        priceHistory.add(new PriceHistoryInfo(Date.from(Instant.now()), price, currency));
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
        priceHistory.add(new PriceHistoryInfo(Date.from(Instant.now()), price, currency));
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        priceHistory.add(new PriceHistoryInfo(Date.from(Instant.now()), price, currency));
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<PriceHistoryInfo> getPriceHistory() {
        return priceHistory;
    }

    public void setPriceHistory(List<PriceHistoryInfo> priceHistory) {
        this.priceHistory = new ArrayList<>(priceHistory);
    }

    @Override
    public Product clone() {
        priceHistory = new ArrayList<>();
        priceHistory.add(new PriceHistoryInfo(Date.from(Instant.now()), price, currency));
        Product product = new Product(
                this.id, this.code, this.description,
                this.price, this.currency, this.stock, this.imageUrl);
        product.setPriceHistory(this.priceHistory.stream().map(PriceHistoryInfo::clone).toList());
        return product;
    }
}