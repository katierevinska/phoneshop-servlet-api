package com.es.phoneshop.model.product;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ArrayListProductDao implements ProductDao {
    private static final ArrayListProductDao instance = new ArrayListProductDao();
    private ArrayList<Product> products = new ArrayList<>();
    private final ReadWriteLock rwProductsLock = new ReentrantReadWriteLock();
    private final Lock readProductsLock = rwProductsLock.readLock();
    private final Lock writeProductsLock = rwProductsLock.writeLock();
    private long idCounter;

    private ArrayListProductDao() {
    }

    public static ArrayListProductDao getInstance() {
        return instance;
    }

    public void setProductsList(ArrayList<Product> products) {
        this.products = products;
    }

    public ArrayList<Product> getProductsList() {
        return products;
    }

    @Override
    public Optional<Product> getProduct(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        readProductsLock.lock();
        try {
            return products.stream()
                    .filter(product -> id.equals(product.getId()))
                    .findAny();
        } finally {
            readProductsLock.unlock();
        }
    }

    @Override
    public List<Product> findProducts() {
        readProductsLock.lock();
        try {
            return products.stream()
                    .filter(product -> product.getPrice() != null && product.getStock() > 0)
                    .toList();
        } finally {
            readProductsLock.unlock();
        }
    }

    @Override
    public Long save(Product product) {
        if (product == null) {
            return null;
        }
        writeProductsLock.lock();
        try {
            Optional<Product> productById = getProduct(product.getId());
            return productById.isEmpty()
                    ? saveProduct(product)
                    : updateProduct(product);
        } finally {
            writeProductsLock.unlock();
        }
    }

    @Override
    public void saveAll(List<Product> products) {
        writeProductsLock.lock();
        try {
            products.forEach(this::save);
        } finally {
            writeProductsLock.unlock();
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        writeProductsLock.lock();
        try {
            products.removeIf(product -> id.equals(product.getId()));
        } finally {
            writeProductsLock.unlock();
        }
    }

    private long saveProduct(Product product) {
        product.setId(++idCounter);
        products.add(product);
        return idCounter;
    }

    private long updateProduct(Product product) {
        delete(product.getId());
        products.add(product);
        return product.getId();
    }
}
