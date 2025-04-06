package com.es.phoneshop.dao;

import com.es.phoneshop.model.product.Product;
import com.es.phoneshop.model.product.SearchResult;
import com.es.phoneshop.model.product.SortField;
import com.es.phoneshop.model.product.SortOrder;
import com.es.phoneshop.utils.ProductSearchUtils;

import java.util.*;
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
    private final Map<SortField, Comparator<SearchResult>> sortFieldComparatorMap = Map.ofEntries(
            Map.entry(SortField.DESCRIPTION, Comparator.comparing(sr -> sr.getProduct().getDescription())),
            Map.entry(SortField.PRICE, Comparator.comparing(sr -> sr.getProduct().getPrice()))
    );

    private ArrayListProductDao() {
    }

    public static ArrayListProductDao getInstance() {
        return instance;
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
                    .findAny()
                    .map(Product::new);
        } finally {
            readProductsLock.unlock();
        }
    }

    @Override
    public List<Product> findProducts(
            String query, String sortFieldStr, String sortOrderStr
    ) {
        readProductsLock.lock();
        try {
            Comparator<SearchResult> sortedComparator;
            if (query == null || query.isEmpty()) {
                sortedComparator = getComparatorBySortFieldStr(sortFieldStr, sortOrderStr);
            } else {
                sortedComparator = Comparator.comparing(SearchResult::getSearchCoefficient);
            }

            return products.stream()
                    .filter(product -> product.getPrice() != null && product.getStock() > 0)
                    .map(product -> ProductSearchUtils.createSearchResult(product, query))
                    .sorted(sortedComparator)
                    .map(SearchResult::getProduct)
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

    private Comparator<SearchResult> getComparatorBySortFieldStr(String sortFieldStr, String sortOrderStr) {
        if (sortFieldStr == null) {
            return Comparator.comparing(pr -> 1);
        }
        SortField sortField = SortField.valueOf(sortFieldStr.toUpperCase());
        Comparator<SearchResult> comparator = sortFieldComparatorMap.get(sortField);

        if (sortOrderStr != null && SortOrder.valueOf(sortOrderStr.toUpperCase()) == SortOrder.DESC) {
            return comparator.reversed();
        }
        return comparator;
    }

    public void setProductsList(ArrayList<Product> products) {
        this.products = products;
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
