package com.es.phoneshop.model.exceptions;

import com.es.phoneshop.model.product.Product;

public class OutOfStockException extends Exception {
    private final Product product;
    private final int requestedStock;
    private final int availableStock;

    public OutOfStockException(Product product, int requestedStock, int availableStock) {
        this.product = product;
        this.requestedStock = requestedStock;
        this.availableStock = availableStock;
    }

    public int getAvailableStock() {
        return availableStock;
    }

    public int getRequestedStock() {
        return requestedStock;
    }

    public Product getProduct() {
        return product;
    }
}
