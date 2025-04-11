package com.es.phoneshop.model.product;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class RecentViewProducts implements Serializable {
    private int limit = 3;
    private final LinkedList<Product> products = new LinkedList<>();

    public synchronized LinkedList<Product> getProducts() {
        return products;
    }
    public synchronized int getLimit() {
        return limit;
    }
    public synchronized void setLimit(int limit) {
        this.limit = limit;
    }
}
