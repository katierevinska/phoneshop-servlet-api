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

    @Override
    public String getMessage() {
        return "out of stock, need " + requestedStock + " but only " + availableStock + " is available";
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
