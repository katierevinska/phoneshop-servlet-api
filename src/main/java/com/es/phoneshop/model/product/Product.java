package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Product implements Serializable {
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
    private transient List<PriceHistoryInfo> priceHistory;

    public Product(
            Long id, String code, String description, BigDecimal price,
            Currency currency, int stock, String imageUrl
    ) {
        this(code, description, price, currency, stock, imageUrl);
        this.id = id;
    }

    public Product(
            String code, String description, BigDecimal price,
            Currency currency, int stock, String imageUrl
    ) {
        initFiends(code, description, price, currency, stock, imageUrl);

        priceHistory = new ArrayList<>();
        priceHistory.add(new PriceHistoryInfo(Date.from(Instant.now()), price, currency));
    }

    public Product(Product original) {
        initFiends(original.getCode(), original.getDescription(), original.getPrice(),
                original.getCurrency(), original.getStock(), original.getImageUrl());

        this.id = original.getId();
        this.priceHistory = original.getPriceHistory().stream()
                .map(PriceHistoryInfo::new)
                .collect(Collectors.toList());
    }

    private void initFiends(
            String code, String description, BigDecimal price,
            Currency currency, int stock, String imageUrl
    ) {
        this.code = code;
        this.description = description;
        this.price = price;
        this.currency = currency;
        this.stock = stock;
        this.imageUrl = imageUrl;
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
        if (this.price != null) {
            priceHistory.add(new PriceHistoryInfo(Date.from(Instant.now()), price, currency));
        }
        this.price = price;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        if (this.currency != null) {
            priceHistory.add(new PriceHistoryInfo(Date.from(Instant.now()), price, currency));
        }
        this.currency = currency;
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
}