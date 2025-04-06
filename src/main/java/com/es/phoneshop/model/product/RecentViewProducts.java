package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.util.LinkedList;

public class RecentViewProducts implements Serializable {
    private int limit = 3;
    private final LinkedList<Product> products = new LinkedList<>();

    public LinkedList<Product> getProducts() {
        return products;
    }
    public int getLimit() {
        return limit;
    }
    public void setLimit(int limit) {
        this.limit = limit;
    }
}
